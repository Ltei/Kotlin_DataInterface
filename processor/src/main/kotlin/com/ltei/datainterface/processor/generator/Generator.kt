package com.ltei.datainterface.processor.generator

import com.ltei.datainterface.processor.model.GenerationConfig
import com.squareup.kotlinpoet.FileSpec

object Generator {
    fun generate(config: GenerationConfig, model: GenerationConfig.Model): FileSpec {
        val builder = FileSpec.builder(model.packageName, "Generated" + model.sourceInterface.simpleName)
        for (obj in OverloadGenerator.generate(config, model))
            builder.addFunction(obj)
        return builder
            .addFunction(ConstructorGenerator.generate(config, model))
            .addType(ImplClassGenerator.generate(model))
            .addType(MutableClassGenerator.generate(config, model))
            .addType(UnsafeClassGenerator.generate(config, model))
            .build()
    }
}