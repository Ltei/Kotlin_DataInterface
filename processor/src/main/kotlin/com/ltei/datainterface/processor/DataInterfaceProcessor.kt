package com.ltei.datainterface.processor

import com.google.auto.service.AutoService
import com.ltei.datainterface.annotation.DataInterface
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(DataInterfaceProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class DataInterfaceProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(DataInterface::class.java).forEach { element ->
            if (element.kind != ElementKind.INTERFACE) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can only be applied to interfaces")
                return false
            }

            val info = buildInfo(element as TypeElement)
            val fileSpec = Generator.generate(info)

            val generationDirectory = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
            fileSpec.writeTo(File(generationDirectory, "${fileSpec.name}.kt"))
        }

        return false
    }

    private fun buildInfo(element: TypeElement): InterfaceConfig {
        return InterfaceConfig(
            packageName = element.asClassName().packageName,
            sourceName = element.simpleName.toString(),
            values = element.enclosedElements.filterIsInstance<ExecutableElement>().map { item ->
                var name = item.simpleName.toString()
                if (name.startsWith("get")) {
                    name = name.removePrefix("get").decapitalize()
                }
                InterfaceConfig.Value(name, item.returnType.asTypeName())
            }
        )
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(DataInterface::class.java.canonicalName)
    }
}