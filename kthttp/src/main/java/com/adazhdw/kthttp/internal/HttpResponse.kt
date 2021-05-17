package com.adazhdw.kthttp.internal

import com.adazhdw.kthttp.HttpClient
import okhttp3.Headers
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type

/**
 * name：HttpResponse
 * author：adazhdw
 * date：2021/3/5:16:48
 * description:
 */
class HttpResponse(private val rawResponse: Response, private val httpClient: HttpClient) : Toable {
    val message: String = rawResponse.message
    val code: Int = rawResponse.code
    val isSuccessful: Boolean = rawResponse.isSuccessful
    val headers: Headers = rawResponse.headers
    private val result: String = bodyToString()

    override fun bodyToString(): String {
        try {
            rawResponse.body?.use {
                return it.string()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    override fun <T> toBean(type: Class<T>): T {
        return doToBean(type)
    }

    override fun <T> toBean(type: Type): T {
        return doToBean(type)
    }

    override fun <T> toBean(typeRef: TypeRef<T>): T {
        return doToBean(typeRef.type)
    }

    private fun <T> doToBean(type: Type): T {
        return httpClient.resultConverter.convert(result, type)
    }

}