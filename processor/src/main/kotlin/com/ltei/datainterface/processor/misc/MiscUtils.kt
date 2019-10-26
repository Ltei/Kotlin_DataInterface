package com.ltei.datainterface.processor.misc

object MiscUtils {
    fun isValidClassName(name: String): Boolean = name.isNotBlank() && name.first().isLetter() && name.all {
        it.isLetterOrDigit() || it == '_'
    }
}