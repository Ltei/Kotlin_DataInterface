package com.ltei.datainterface.processor.model

import com.ltei.datainterface.annotation.DataInterface
import com.ltei.datainterface.processor.misc.MiscUtils
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.annotations.Nullable
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

object GenerationConfigBuilder {
    fun build(elements: List<Element>): GenerationConfig {
        val tempConfigs = elements.map { element ->
            if (element.kind != ElementKind.INTERFACE)
                throw IllegalArgumentException("Can only be applied to interfaces")
            getTempConfig(element as TypeElement)
        }

        val dataInterfaceConfigs = tempConfigs.map { tempConfig ->
            getConfig(tempConfig, tempConfigs)
        }

        return GenerationConfig(dataInterfaceConfigs)
    }

    private fun getTempConfig(element: TypeElement): TempDataInterfaceConfig {
        val annotation = element.getAnnotation(DataInterface::class.java)

        require(annotation.builderName == "" || MiscUtils.isValidClassName(annotation.builderName)) { "The defined interface name isn't valid." }
        require(annotation.implClassName == "" || MiscUtils.isValidClassName(annotation.implClassName)) { "The defined impl class name isn't valid." }
        require(annotation.mutableClassName == "" || MiscUtils.isValidClassName(annotation.mutableClassName)) { "The defined mutable class name isn't valid." }
        require(annotation.unsafeClassName == "" || MiscUtils.isValidClassName(annotation.unsafeClassName)) { "The defined unsafe class name isn't valid." }

        val sourceName = element.simpleName.toString()

        return TempDataInterfaceConfig(
            element = element,
            packageName = element.asClassName().packageName,
            sourceName = sourceName,
            generatedBuilderName = if (annotation.builderName.isEmpty()) "${sourceName}Builder"
            else annotation.builderName,
            generatedImplName = if (annotation.implClassName.isEmpty()) "${sourceName}Impl"
            else annotation.implClassName,
            generatedMutableName = if (annotation.mutableClassName.isEmpty()) "Mutable$sourceName"
            else annotation.mutableClassName,
            generatedUnsafeName = if (annotation.unsafeClassName.isEmpty()) "Unsafe$sourceName"
            else annotation.unsafeClassName
        )
    }

    private fun getConfig(
        tempConfig: TempDataInterfaceConfig,
        allTempConfigs: List<TempDataInterfaceConfig>
    ): GenerationConfig.Model {
        return GenerationConfig.Model(
            packageName = tempConfig.packageName,
            sourceInterface = tempConfig.sourceInterface,
            generatedBuilder = tempConfig.generatedBuilder,
            generatedImpl = tempConfig.generatedImpl,
            generatedMutable = tempConfig.generatedMutable,
            generatedUnsafe = tempConfig.generatedUnsafe,
            values = tempConfig.element.enclosedElements.filterIsInstance<ExecutableElement>().map { item ->
                var name = item.simpleName.toString()
                if (name.startsWith("get")) {
                    name = name.removePrefix("get").decapitalize()
                }

                // Check if is nullable
                val nullableAnnotation = item.getAnnotation(Nullable::class.java)
                val nullable = nullableAnnotation != null

                var typeName = item.returnType.asTypeName()
                var typeNameString = typeName.toString()
                if (typeNameString == "error.NonExistentClass") {
                    throw RuntimeException("Failed to resolve type of $name, in ${tempConfig.sourceInterface.simpleName} : Add 'correctErrorTypes = true' as a kapt option in your build.gradle to enable resolution of generated types.")
                } else if (typeNameString.split(".").size <= 1) {
                    // It's a generated class name, lets try to resolve it
                    for (otherConfig in allTempConfigs) {
                        val foundClassInfo = when (typeNameString) {
                            otherConfig.generatedBuilder.simpleName -> otherConfig.generatedBuilder
                            otherConfig.generatedImpl.simpleName -> otherConfig.generatedImpl
                            otherConfig.generatedMutable.simpleName -> otherConfig.generatedMutable
                            otherConfig.generatedUnsafe.simpleName -> otherConfig.generatedUnsafe
                            else -> null
                        }
                        if (foundClassInfo != null) {
                            typeName = foundClassInfo
                            typeNameString = typeName.toString()
                            break
                        }
                    }
                }

                if (typeNameString.split(".").size <= 1)
                    throw IllegalStateException()

                GenerationConfig.Value(name, typeName.copy(nullable = nullable))
            }
        )
    }

    class TempDataInterfaceConfig(
        val element: TypeElement,
        val packageName: String,
        sourceName: String,
        generatedBuilderName: String,
        generatedImplName: String,
        generatedMutableName: String,
        generatedUnsafeName: String
    ) {
        val sourceInterface = ClassName(packageName, sourceName)
        val generatedBuilder = ClassName(packageName, generatedBuilderName)
        val generatedImpl = ClassName(packageName, generatedImplName)
        val generatedMutable = ClassName(packageName, generatedMutableName)
        val generatedUnsafe = ClassName(packageName, generatedUnsafeName)
    }
}