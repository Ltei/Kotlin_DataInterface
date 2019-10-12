package com.ltei.datainterface.processor

import com.ltei.datainterface.annotation.DataInterface
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

fun TypeName.toKotlin(): TypeName {
    val result = when (this) {
        is ClassName -> when (this.simpleName) {
            "Boolean" -> com.squareup.kotlinpoet.BOOLEAN
            "Byte" -> com.squareup.kotlinpoet.BYTE
            "Short" -> com.squareup.kotlinpoet.SHORT
            "Int", "Integer" -> com.squareup.kotlinpoet.INT
            "Long" -> com.squareup.kotlinpoet.LONG
            "Float" -> com.squareup.kotlinpoet.FLOAT
            "Double" -> com.squareup.kotlinpoet.DOUBLE
            "String" -> com.squareup.kotlinpoet.STRING
            else -> this
        }
        is ParameterizedTypeName -> when {
            ClassNames.isList(this.rawType) -> ClassNames.listOf(this.typeArguments[0].toKotlin())
            ClassNames.isSet(this.rawType) -> ClassNames.setOf(this.typeArguments[0].toKotlin())
            ClassNames.isMap(this.rawType) -> ClassNames.mapOf(
                this.typeArguments[0].toKotlin(),
                this.typeArguments[1].toKotlin()
            )
            else -> this
        }
        else -> this
    }
    return result.copy(nullable = this.isNullable)
}

fun TypeName.toMutable(annotation: DataInterface): TypeName {
    val toKotlin = this.toKotlin()
    val result = when (toKotlin) {
        is ClassName -> toKotlin
        is ParameterizedTypeName -> if (annotation.mutableCollections) {
            when {
                ClassNames.isList(toKotlin.rawType) -> ClassNames.mutableListOf(toKotlin.typeArguments[0].toMutable(annotation))
                ClassNames.isSet(toKotlin.rawType) -> ClassNames.mutableSetOf(toKotlin.typeArguments[0].toMutable(annotation))
                ClassNames.isMap(toKotlin.rawType) -> ClassNames.mutableMapOf(
                    toKotlin.typeArguments[0].toMutable(annotation),
                    toKotlin.typeArguments[1].toMutable(annotation)
                )
                else -> toKotlin
            }
        } else toKotlin
        else -> toKotlin
    }
    return result.copy(nullable = this.isNullable)
}

fun TypeName.castToMutableStatement(annotation: DataInterface, valueName: String): String = when (this) {
    is ClassName -> when (this.simpleName) {
        "Boolean", "Byte", "Short", "Int", "Long", "Float", "Double", "String" -> valueName
        else -> "$valueName.toMutable()"
    }
    is ParameterizedTypeName -> if (annotation.mutableCollections) {
        when {
            ClassNames.isList(this.rawType) -> "$valueName.toMutableList()"
            ClassNames.isSet(this.rawType) -> "$valueName.toMutableSet()"
            ClassNames.isMap(this.rawType) -> "$valueName.toMutableMap()"
            else -> valueName
        }
    } else valueName
    else -> valueName
}