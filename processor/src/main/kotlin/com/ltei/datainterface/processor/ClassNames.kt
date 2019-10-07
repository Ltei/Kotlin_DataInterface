package com.ltei.datainterface.processor

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

object ClassNames {

    fun isList(type: TypeName) = isCollection("List", type)
    fun isSet(type: TypeName) = isCollection("Set", type)
    fun isMap(type: TypeName) = isCollection("Map", type)

    fun listOf(type: TypeName) = com.squareup.kotlinpoet.LIST.parameterizedBy(type)
    fun mutableListOf(type: TypeName) = com.squareup.kotlinpoet.MUTABLE_LIST.parameterizedBy(type)

    fun setOf(type: TypeName) = com.squareup.kotlinpoet.SET.parameterizedBy(type)
    fun mutableSetOf(type: TypeName) = com.squareup.kotlinpoet.MUTABLE_SET.parameterizedBy(type)

    fun mapOf(keyType: TypeName, valueType: TypeName) = com.squareup.kotlinpoet.MAP.parameterizedBy(keyType, valueType)
    fun mutableMapOf(keyType: TypeName, valueType: TypeName) = com.squareup.kotlinpoet.MUTABLE_MAP.parameterizedBy(keyType, valueType)

    private fun isCollection(collection: String, type: TypeName): Boolean {
        val str = type.toString()
        return str == "java.util.$collection" || str == "kotlin.collections.$collection"
    }

}