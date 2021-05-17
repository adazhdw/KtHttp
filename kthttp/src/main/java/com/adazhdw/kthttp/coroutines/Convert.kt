package com.adazhdw.kthttp.coroutines

import com.adazhdw.kthttp.Https
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type

/**
 * author：daguozhu
 * date-time：2020/11/18 11:18
 * description：
 **/

@Throws(IOException::class)
fun <R> Response.convert(type: Type?): R {
    val converter = Https.httpClient.resultConverter
    return converter.convert(this, type)
}