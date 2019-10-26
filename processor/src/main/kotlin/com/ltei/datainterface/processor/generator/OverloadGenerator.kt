package com.ltei.datainterface.processor.generator

import com.ltei.datainterface.processor.generator.GenerationUtils.buildValuesParamString
import com.ltei.datainterface.processor.model.GenerationConfig
import com.squareup.kotlinpoet.FunSpec

object OverloadGenerator {
    fun generate(config: GenerationConfig, model: GenerationConfig.Model): List<FunSpec> {
        fun buildToMutableFun(): FunSpec {
            val result = FunSpec.builder("toMutable").returns(model.generatedMutable)
                .receiver(model.sourceInterface)
                .addStatement(
                    "return %T(${buildValuesParamString(config, model, toMutable = true)})",
                    model.generatedMutable
                )
            return result.build()
        }

        fun buildToUnsafeFun(): FunSpec {
            val result = FunSpec.builder("toUnsafe")
                .receiver(model.sourceInterface)
                .returns(model.generatedUnsafe)
                .addStatement(
                    "return %T(${buildValuesParamString(config, model, toMutable = true)})",
                    model.generatedUnsafe
                )
            return result.build()
        }

        fun buildDebugFun(): FunSpec {
            val code = StringBuilder("println(\"")
            for (value in model.values)
                code.append("${value.name}=\${this.${value.name}}\\n")
            code.append("\")")

            val result = FunSpec.builder("debug")
                .receiver(model.sourceInterface)
                .addCode(code.toString())
            return result.build()
        }

        return listOf(
            buildToMutableFun(),
            buildToUnsafeFun(),
            buildDebugFun()
        )
    }
}