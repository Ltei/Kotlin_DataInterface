package com.ltei.datainterface.test

import com.ltei.datainterface.annotation.DataInterface

@DataInterface
interface IModel {
    val int: Int
    val long: Long
    val list: List<Int>
}

fun main() {
    Model.new(0, 0L, listOf())
}