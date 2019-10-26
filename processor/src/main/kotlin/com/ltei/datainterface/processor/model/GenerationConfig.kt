package com.ltei.datainterface.processor.model

import com.ltei.datainterface.processor.misc.ClassNames
import com.ltei.datainterface.processor.misc.toKotlin
import com.ltei.datainterface.processor.misc.toMutable
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

class GenerationConfig(
    val models: List<Model>
) {
    fun getGeneratedClassType(typeName: TypeName): GeneratedClassType? {
        val className = when (typeName) {
            is ClassName -> typeName
            is ParameterizedTypeName -> typeName.rawType
            else -> return null
        }
        for (dataInterface in models) {
            when (className) {
                dataInterface.generatedImpl -> return GeneratedClassType.Impl
                dataInterface.generatedMutable -> return GeneratedClassType.Mutable
                dataInterface.generatedUnsafe -> return GeneratedClassType.Unsafe
                dataInterface.generatedBuilder -> return GeneratedClassType.Builder
            }
        }
        return null
    }

    fun buildCastToMutableStatement(valueName: String, typeName: TypeName): String = when {
        getGeneratedClassType(typeName) == GeneratedClassType.Impl -> "$valueName.toMutable()"
        typeName is ClassName -> valueName /*when (typeName.simpleName) {
            "Boolean", "Byte", "Short", "Int", "Long", "Float", "Double", "String" -> valueName
            else -> "$valueName.toMutable()"
        }*/
        typeName is ParameterizedTypeName -> when {
            ClassNames.isList(typeName.rawType) -> "$valueName.toMutableList()"
            ClassNames.isSet(typeName.rawType) -> "$valueName.toMutableSet()"
            ClassNames.isMap(typeName.rawType) -> "$valueName.toMutableMap()"
            else -> valueName
        }
        else -> valueName
    }

    class Model(
        val packageName: String,
        val sourceInterface: ClassName,
        val generatedBuilder: ClassName,
        val generatedImpl: ClassName,
        val generatedMutable: ClassName,
        val generatedUnsafe: ClassName,
        val values: List<Value>
    )

    class Value(
        val name: String,
        type: TypeName,
        mutableType: TypeName = type.toMutable()
    ) {
        val type: TypeName = type.toKotlin()
        val mutableType: TypeName = mutableType.toKotlin()
    }
}