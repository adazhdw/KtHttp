package com.adazhdw.kthttp.coroutines.parser

import com.adazhdw.kthttp.util.ClazzType
import com.adazhdw.kthttp.util.Preconditions
import java.lang.reflect.Type

/**
 * author：daguozhu
 * date-time：2020/11/18 11:04
 * description：
 **/
abstract class ParserImpl<T> : Parser<T> {

    @JvmField
    protected val mType: Type

    /**
     * 此构造方法适用于任意Class对象，但更多用于带泛型的Class对象
     */
    constructor() {
        mType = getSuperclassTypeParameter(javaClass)
    }

    /**
     * 此构造方法仅适用于不带泛型的Class对象
     */
    constructor(type: Type) {
        mType = ClazzType.canonicalize(Preconditions.checkNotNull(type))
    }

    /**
     * 获取当前泛型的type
     */
    private fun getSuperclassTypeParameter(subclass: Class<*>, index: Int = 0): Type {
        return ClazzType.getType(subclass, index)
    }

}