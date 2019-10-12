package com.ltei.datainterface.processor

import com.ltei.datainterface.annotation.DataInterface
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

class InterfaceConfig(
    annotation: DataInterface,
    val packageName: String,
    sourceName: String,
    rawName: String = getRawName(sourceName),
    generatedInterfaceName: String = if (annotation.interfaceName.isEmpty()) {
        if (isRawName(sourceName)) "I$rawName" else rawName
    } else annotation.interfaceName,
    generatedImplName: String = if (annotation.implClassName.isEmpty()) {
        "${rawName}Impl"
    } else annotation.implClassName,
    generatedMutableName: String = if (annotation.mutableClassName.isEmpty()) {
        "Mutable$rawName"
    } else annotation.mutableClassName,
    generatedUnsafeName: String = if (annotation.unsafeClassName.isEmpty()) {
        "Unsafe$rawName"
    } else annotation.unsafeClassName,
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
        annotation: DataInterface,
        val name: String,
        type: TypeName,
        mutableType: TypeName = type.toMutable(annotation)
    ) {
        val type: TypeName = type.toKotlin()
        val mutableType: TypeName = mutableType.toKotlin()
        val nullableMutableType: TypeName = mutableType.copy(nullable = true)
        val castToMutableStatement: String = type.castToMutableStatement(annotation, name)
    }
}