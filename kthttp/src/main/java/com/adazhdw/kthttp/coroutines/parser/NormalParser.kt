package com.adazhdw.kthttp.coroutines.parser

import com.adazhdw.kthttp.coroutines.convert
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type

/**
 * author：daguozhu
 * date-time：2020/11/18 11:12
 * description：将Response对象解析成泛型T对象
 **/
open class NormalParser<T> : ParserImpl<T> {
    /**
     * 此构造方法适用于任意Class对象，但更多用于带泛型的Class对象
     */
    protected constructor() : super()

    /**
     * 此构造方法仅适用于不带泛型的Class对象
     */
    constructor(type: Type) : super(type)

    @Throws(IOException::class)
    override fun parse(response: Response): T {
        return response.convert(mType)
    }

    companion object {
        operator fun <T : Any> get(type: Class<T>) = NormalParser<T>(type)
    }
}