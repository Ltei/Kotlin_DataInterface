package com.ltei.datainterface.processor.misc

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

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
            ClassNames.isList(this.rawType) -> LIST.parameterizedBy(this.typeArguments[0].toKotlin())
            ClassNames.isMutableList(this.rawType) -> MUTABLE_LIST.parameterizedBy(this.typeArguments[0].toKotlin())

            ClassNames.isSet(this.rawType) -> SET.parameterizedBy(this.typeArguments[0].toKotlin())
            ClassNames.isMutableSet(this.rawType) -> MUTABLE_SET.parameterizedBy(this.typeArguments[0].toKotlin())

            ClassNames.isMap(this.rawType) -> MAP.parameterizedBy(
                this.typeArguments[0].toKotlin(),
                this.typeArguments[1].toKotlin()
            )
            ClassNames.isMutableMap(this.rawType) -> MUTABLE_MAP.parameterizedBy(
                this.typeArguments[0].toKotlin(),
                this.typeArguments[1].toKotlin()
            )

            else -> this
        }
        else -> this
    }
    return result.copy(nullable = this.isNullable)
}

fun TypeName.toMutable(): TypeName {
    val toKotlin = this.toKotlin()
    val result = when (toKotlin) {
        is ClassName -> toKotlin
        is ParameterizedTypeName -> when {
            ClassNames.isList(toKotlin.rawType) -> MUTABLE_LIST.parameterizedBy(toKotlin.typeArguments[0].toMutable())
            ClassNames.isSet(toKotlin.rawType) -> MUTABLE_SET.parameterizedBy(toKotlin.typeArguments[0].toMutable())
            ClassNames.isMap(toKotlin.rawType) -> MUTABLE_MAP.parameterizedBy(
                toKotlin.typeArguments[0].toMutable(),
                toKotlin.typeArguments[1].toMutable()
            )
            else -> toKotlin
        }
        else -> toKotlin
    }
    return result.copy(nullable = this.isNullable)
}