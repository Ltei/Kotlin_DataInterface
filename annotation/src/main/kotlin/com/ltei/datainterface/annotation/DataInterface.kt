package com.ltei.datainterface.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class DataInterface(
    val builderName: String = "",
    val implClassName: String = "",
    val mutableClassName: String = "",
    val unsafeClassName: String = ""
)