package com.adazhdw.kthttp.internal

import com.adazhdw.kthttp.util.ClazzType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class TypeRef<T> {
    val type: Type

    init {
        val superClass = javaClass.genericSuperclass
        type = ClazzType.getType(superClass,0)
    }
}