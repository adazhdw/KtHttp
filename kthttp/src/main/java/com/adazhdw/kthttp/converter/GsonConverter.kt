package com.adazhdw.kthttp.converter

import com.adazhdw.kthttp.OkConfig
import com.adazhdw.kthttp.util.GsonUtils
import com.google.gson.Gson
import okhttp3.Response
import java.lang.reflect.Type

/**
 * author：daguozhu
 * date-time：2020/11/3 19:05
 * description：
 **/
class GsonConverter private constructor(private val gson: Gson) : IConverter {

    companion object {
        fun create(): GsonConverter {
            return GsonConverter(GsonUtils.buildGson())
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> convert(response: Response, type: Type?, needDecodeResult: Boolean): T {
        val body = response.body ?: throw Exception("okhttp3.Response's body is null")
        var result2 = body.string()
        if (needDecodeResult) {
            result2 = OkConfig.config.coder.decode(result2)
        }
        if (type === String::class.java) return result2 as T
        return gson.fromJson(result2, type)
    }

}