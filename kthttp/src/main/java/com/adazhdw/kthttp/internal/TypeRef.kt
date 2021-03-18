package com.adazhdw.kthttp.internal

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class TypeRef<T> {
    val type: Type

    init {
        val superClass = javaClass.genericSuperclass
        type = (superClass as ParameterizedType).actualTypeArguments[0]
    }
}