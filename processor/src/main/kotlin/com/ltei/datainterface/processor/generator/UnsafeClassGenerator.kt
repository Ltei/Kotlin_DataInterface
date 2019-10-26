package com.ltei.datainterface.processor.generator

import com.ltei.datainterface.processor.generator.GenerationUtils.buildValuesNullableCheckString
import com.ltei.datainterface.processor.generator.GenerationUtils.buildValuesParamString
import com.ltei.datainterface.processor.model.GenerationConfig
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

object UnsafeClassGenerator {
    fun generate(config: GenerationConfig, model: GenerationConfig.Model): TypeSpec {
        fun buildToSafeOrNullFun(): FunSpec {
            val code = StringBuilder()
            for (value in model.values) {
                code.append("|val ${value.name} = this.${value.name}\n")
            }
            code.append(
                """
                |return if (${buildValuesNullableCheckString(model)}) {
                |   %T(${buildValuesParamString(config, model, toMutable = false)})
                |} else null
                |"""
            )
            val result = FunSpec.builder("toSafeOrNull")
                .returns(model.generatedMutable.copy(nullable = true))
                .addCode(code.toString().trimMargin(), model.generatedMutable)
            return result.build()
        }

        fun buildCloneFromFun(): FunSpec {
            val code = StringBuilder()
            for (value in model.values)
                code.append(
                    "|this.${value.name} = ${config.buildCastToMutableStatement(
                        "obj.${value.name}",
                        value.type
                    )}\n"
                )

            val result = FunSpec.builder("cloneFrom")
                .addParameter(ParameterSpec("obj", model.sourceInterface))
                .addCode(code.toString().trimMargin())
            return result.build()
        }

        fun buildCloneFromMutableFun(): FunSpec {
            val code = StringBuilder()
            for (value in model.values)
                code.append("|this.${value.name} = obj.${value.name}\n")

            val result = FunSpec.builder("cloneFrom")
                .addParameter(ParameterSpec("obj", model.generatedMutable))
                .addCode(code.toString().trimMargin())
            return result.build()
        }

        fun buildCloneFromUnsafeFun(): FunSpec {
            val code = StringBuilder()
            for (value in model.values)
                code.append("|this.${value.name} = obj.${value.name}\n")

            val result = FunSpec.builder("cloneFrom")
                .addParameter(ParameterSpec("obj", model.generatedUnsafe))
                .addCode(code.toString().trimMargin())
            return result.build()
        }

        val result = TypeSpec.classBuilder(model.generatedUnsafe)
            .addFunction(buildToSafeOrNullFun())
            .addFunction(buildCloneFromFun())
            .addFunction(buildCloneFromMutableFun())
            .addFunction(buildCloneFromUnsafeFun())
        val constructor = FunSpec.constructorBuilder()
        for (value in model.values) {
            result.addProperty(
                PropertySpec.builder(
                    value.name,
                    value.mutableType.copy(nullable = true)
                ).initializer(value.name).mutable().build()
            )
            constructor.addParameter(
                ParameterSpec.builder(value.name, value.mutableType.copy(nullable = true)).defaultValue("null").build()
            )

        }
        result.primaryConstructor(constructor.build())
        return result.build()
    }
}