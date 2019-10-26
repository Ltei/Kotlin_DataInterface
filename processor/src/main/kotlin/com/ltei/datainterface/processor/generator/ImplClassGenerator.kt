package com.ltei.datainterface.processor.generator

import com.ltei.datainterface.processor.model.GenerationConfig
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

object ImplClassGenerator {
    fun generate(model: GenerationConfig.Model): TypeSpec {
        val result = TypeSpec.classBuilder(model.generatedImpl)
            .addModifiers(KModifier.PRIVATE)
            .addSuperinterface(model.sourceInterface)
        val constructor = FunSpec.constructorBuilder()
        for (value in model.values) {
            result.addProperty(
                PropertySpec.builder(value.name, value.type, KModifier.OVERRIDE)
                    .initializer(value.name).build()
            )
            constructor.addParameter(value.name, value.type)
        }
        result.primaryConstructor(constructor.build())
        return result.build()
    }
}