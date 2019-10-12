package com.ltei.datainterface.processor

object MiscUtils {
    fun isValidClassName(name: String): Boolean = name.isNotBlank() && name.first().isLetter() && name.all {
        it.isLetterOrDigit() || it == '_'
    }
}