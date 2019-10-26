package com.ltei.datainterface.processor.generator

import com.ltei.datainterface.processor.model.GenerationConfig
import com.squareup.kotlinpoet.FunSpec

object ConstructorGenerator {
    fun generate(config: GenerationConfig, model: GenerationConfig.Model): FunSpec {
        val builder =  FunSpec.builder(model.sourceInterface.simpleName)
            .returns(model.sourceInterface)
            .addStatement(
                "return %T(${GenerationUtils.buildValuesParamString(config, model, toMutable = false)})",
                model.generatedImpl
            )
        for (value in model.values)
            builder.addParameter(value.name, value.type)
        return builder.build()
    }
}