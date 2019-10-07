package com.ltei.datainterface.processor

import com.squareup.kotlinpoet.*

object Generator {

    fun generate(config: InterfaceConfig): FileSpec {
        return FileSpec.builder(config.packageName, config.generatedInterface.name)
            .addType(buildMainInterface(config))
            .addType(buildImplClass(config))
            .addType(buildMutableClass(config))
            .addType(buildUnsafeClass(config))
            .build()
    }

    // Private builders

    // - main interface

    private fun buildMainInterface(config: InterfaceConfig): TypeSpec {
        fun buildToMutableFun(config: InterfaceConfig): FunSpec {
            val result = FunSpec.builder("toMutable").returns(config.generatedMutable.className)
                .addStatement(
                    "return %T(${buildValuesParamString(config, toMutable = true)})",
                    config.generatedMutable.className
                )
            return result.build()
        }
        fun buildToUnsafeFun(config: InterfaceConfig): FunSpec {
            val result = FunSpec.builder("toUnsafe")
                .returns(config.generatedUnsafe.className)
                .addStatement(
                    "return %T(${buildValuesParamString(config, toMutable = true)})",
                    config.generatedUnsafe.className
                )
            return result.build()
        }

        fun buildCompanion(config: InterfaceConfig): TypeSpec {
            val newBuilder = FunSpec.builder("new")
                .returns(config.generatedInterface.className)
                .addStatement(
                    "return %T(${buildValuesParamString(config, toMutable = false)})",
                    config.generatedImpl.className
                )
            val newMutableBuilder = FunSpec.builder("newMutable").returns(config.generatedMutable.className)
                .addStatement(
                    "return %T(${buildValuesParamString(config, toMutable = false)})",
                    config.generatedMutable.className
                )

            // Set values
            for (value in config.values) {
                newBuilder.addParameter(value.name, value.type)
                newMutableBuilder.addParameter(value.name, value.mutableType)
            }

            return TypeSpec.companionObjectBuilder()
                .addFunction(newBuilder.build())
                .addFunction(newMutableBuilder.build()).build()
        }

        val builder = TypeSpec.interfaceBuilder(config.generatedInterface.className)
            .addSuperinterface(config.sourceInterface.className)
            .addType(buildCompanion(config))
            .addFunction(buildToMutableFun(config))
            .addFunction(buildToUnsafeFun(config))
        return builder.build()
    }

    // - impl

    private fun buildImplClass(config: InterfaceConfig): TypeSpec {
        val result = TypeSpec.classBuilder(config.generatedImpl.className)
            .addModifiers(KModifier.PRIVATE)
            .addSuperinterface(config.generatedInterface.className)
        val constructor = FunSpec.constructorBuilder()
        for (value in config.values) {
            result.addProperty(
                PropertySpec.builder(value.name, value.type, KModifier.OVERRIDE)
                    .initializer(value.name).build()
            )
            constructor.addParameter(value.name, value.type)
        }
        result.primaryConstructor(constructor.build())
        return result.build()
    }

    // - mutable

    private fun buildMutableClass(config: InterfaceConfig): TypeSpec {
        fun buildToUnsafeFun(config: InterfaceConfig): FunSpec {
            val result = FunSpec.builder("toUnsafe")
                .returns(config.generatedUnsafe.className)
                .addModifiers(KModifier.OVERRIDE)
                .addStatement(
                    "return %T(${buildValuesParamString(config, toMutable = false)})",
                    config.generatedUnsafe.className
                )
            return result.build()
        }

        val result = TypeSpec.classBuilder(config.generatedMutable.className)
            .addSuperinterface(config.generatedInterface.className)
            .addFunction(buildToUnsafeFun(config))
        val constructor = FunSpec.constructorBuilder()
        for (value in config.values) {
            result.addProperty(
                PropertySpec.builder(value.name, value.mutableType, KModifier.OVERRIDE)
                    .initializer(value.name).mutable().build()
            )
            constructor.addParameter(value.name, value.mutableType)
        }
        result.primaryConstructor(constructor.build())
        return result.build()
    }

    // - unsafe

    private fun buildUnsafeClass(config: InterfaceConfig): TypeSpec {
        fun buildToSafeFun(config: InterfaceConfig): FunSpec {
            val code = StringBuilder()
            for (value in config.values) {
                code.append("|val ${value.name} = this.${value.name}\n")
            }
            code.append(
                """
                |return if (${buildValuesNullableCheckString(config)}) {
                |   %T(${buildValuesParamString(config, toMutable = false)})
                |} else null
                |"""
            )
            val result = FunSpec.builder("toSafe")
                .returns(config.generatedMutable.className.copy(nullable = true))
                .addCode(code.toString().trimMargin(), config.generatedMutable.className)
            return result.build()
        }

        val result = TypeSpec.classBuilder(config.generatedUnsafe.className)
            .addFunction(buildToSafeFun(config))
        val constructor = FunSpec.constructorBuilder()
        for (value in config.values) {
            result.addProperty(
                PropertySpec.builder(value.name, value.nullableMutableType).initializer(value.name).mutable().build()
            )
            constructor.addParameter(
                ParameterSpec.builder(value.name, value.nullableMutableType).defaultValue("null").build()
            )

        }
        result.primaryConstructor(constructor.build())
        return result.build()
    }

    // Misc

    private fun buildValuesNullableCheckString(config: InterfaceConfig): String {
        val builder = StringBuilder()
        for ((idx, value) in config.values.withIndex()) {
            builder.append(value.name).append(" != null")
            if (idx < config.values.lastIndex) builder.append(" && ")
        }
        return builder.toString()
    }

    private fun buildValuesParamString(config: InterfaceConfig, toMutable: Boolean): String {
        val builder = StringBuilder()
        for ((idx, value) in config.values.withIndex()) {
            if (toMutable) builder.append(value.castToMutableStatement) else builder.append(value.name)
            if (idx < config.values.lastIndex) builder.append(", ")
        }
        return builder.toString()
    }

}