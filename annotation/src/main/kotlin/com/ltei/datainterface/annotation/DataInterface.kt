package com.ltei.datainterface.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class DataInterface(
    val interfaceName: String = "",
    val implClassName: String = "",
    val mutableClassName: String = "",
    val unsafeClassName: String = "",
    val mutableCollections: Boolean = true
)