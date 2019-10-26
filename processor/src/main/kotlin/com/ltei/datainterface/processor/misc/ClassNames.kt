package com.ltei.datainterface.processor.misc

import com.squareup.kotlinpoet.TypeName

object ClassNames {

    fun isList(type: TypeName) = isCollection("List", type)
    fun isMutableList(type: TypeName) = isCollection("MutableList", type)

    fun isSet(type: TypeName) = isCollection("Set", type)
    fun isMutableSet(type: TypeName) = isCollection("MutableSet", type)

    fun isMap(type: TypeName) = isCollection("Map", type)
    fun isMutableMap(type: TypeName) = isCollection("MutableMap", type)

    private fun isCollection(collection: String, type: TypeName): Boolean {
        val str = type.toString()
        return str == "java.util.$collection" || str == "kotlin.collections.$collection"
    }

}