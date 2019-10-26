package com.ltei.datainterface.processor

import com.google.auto.service.AutoService
import com.ltei.datainterface.annotation.DataInterface
import com.ltei.datainterface.processor.generator.Generator
import com.ltei.datainterface.processor.model.GenerationConfigBuilder
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(DataInterfaceProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class DataInterfaceProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val config =
            GenerationConfigBuilder.build(roundEnv.getElementsAnnotatedWith(DataInterface::class.java).toList())

        for (model in config.models) {
            val fileSpec = Generator.generate(config, model)
            val generationDirectory = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
            fileSpec.writeTo(File(generationDirectory, "${fileSpec.name}.kt"))
        }

        return false
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(DataInterface::class.java.canonicalName)
    }
}