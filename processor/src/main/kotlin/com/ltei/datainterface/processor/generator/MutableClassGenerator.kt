package com.ltei.datainterface.processor.generator

import com.ltei.datainterface.processor.generator.GenerationUtils.buildValuesParamString
import com.ltei.datainterface.processor.model.GenerationConfig
import com.squareup.kotlinpoet.*

object MutableClassGenerator {
    fun generate(config: GenerationConfig, model: GenerationConfig.Model): TypeSpec {
        fun buildToUnsafeFun(): FunSpec {
            val result = FunSpec.builder("toUnsafe")
                .returns(model.generatedUnsafe)
                .addStatement(
                    "return %T(${buildValuesParamString(config, model, toMutable = false)})",
                    model.generatedUnsafe
                )
            return result.build()
        }

        fun buildCloneFromFun(): FunSpec {
            val code = StringBuilder()
            for (value in model.values)
                code.append("|this.${value.name} = ${config.buildCastToMutableStatement("obj.${value.name}", value.type)}\n")

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

        val result = TypeSpec.classBuilder(model.generatedMutable)
            .addSuperinterface(model.sourceInterface)
            .addFunction(buildToUnsafeFun())
            .addFunction(buildCloneFromFun())
            .addFunction(buildCloneFromMutableFun())
        val constructor = FunSpec.constructorBuilder()
        for (value in model.values) {
            result.addProperty(
                PropertySpec.builder(value.name, value.mutableType, KModifier.OVERRIDE)
                    .initializer(value.name).mutable().build()
            )
            constructor.addParameter(value.name, value.mutableType)
        }
        result.primaryConstructor(constructor.build())
        return result.build()
    }
}