package com.ltei.datainterface.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

class InterfaceConfig(
    val packageName: String,
    sourceName: String,
    rawName: String = getRawName(sourceName),
    generatedInterfaceName: String = if (isRawName(sourceName)) "I$rawName" else rawName,
    generatedImplName: String = "${rawName}Impl",
    generatedMutableName: String = "Mutable$rawName",
    generatedUnsafeName: String = "Unsafe$rawName",
    val values: List<Value>
) {
    companion object {
        private fun isRawName(name: String) = !(name[0] == 'I' && name[1].isUpperCase())
        private fun getRawName(name: String) = if (isRawName(name)) name else name.removeRange(0..0).capitalize()
    }

    val sourceInterface = ClassInfo(sourceName)
    val generatedInterface = ClassInfo(generatedInterfaceName)
    val generatedImpl = ClassInfo(generatedImplName)
    val generatedMutable = ClassInfo(generatedMutableName)
    val generatedUnsafe = ClassInfo(generatedUnsafeName)

    inner class ClassInfo(val name: String) {
        val className = ClassName(packageName, name)
    }

    class Value(
        val name: String,
        type: TypeName,
        mutableType: TypeName = type.toMutable()
    ) {
        val type: TypeName = type.toKotlin()
        val mutableType: TypeName = mutableType.toKotlin()
        val nullableMutableType: TypeName = mutableType.copy(nullable = true)
        val castToMutableStatement: String = type.castToMutableStatement(name)
    }
}