package com.adazhdw.kthttp.converter

import com.adazhdw.kthttp.util.GsonUtils
import com.google.gson.Gson
import okhttp3.Response
import java.lang.reflect.Type

/**
 * author：daguozhu
 * date-time：2020/11/3 19:05
 * description：
 **/
class GsonConverter private constructor(private val gson: Gson) : Converter {

    companion object {
        fun create(): GsonConverter {
            return GsonConverter(GsonUtils.buildGson())
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> convert(response: Response, type: Type?): T {
        val body = response.body ?: throw Exception("okhttp3.Response's body is null")
        val result = body.string()
        if (type === String::class.java) return result as T
        return gson.fromJson(result, type)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> convert(result: String, type: Type?): T {
        if (type === String::class.java) return result as T
        return gson.fromJson(result, type)
    }

}