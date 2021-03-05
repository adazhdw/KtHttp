package com.adazhdw.kthttp.request

import com.adazhdw.kthttp.callback.RequestCallback
import com.adazhdw.kthttp.constant.BodyType
import com.adazhdw.kthttp.constant.Method
import okhttp3.Call
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * FileName: IRequest
 * Author: adazhdw
 * Date: 2021/1/13 8:57
 * Description: Request 抽象方法提取
 * History:
 */
interface IRequest<Req : IRequest<Req>> {

    fun url(url: String): Req
    fun method(method: Method): Req
    fun bodyType(bodyType: BodyType): Req
    fun setUrlEncoder(urlEncoder: Boolean): Req
    fun setNeedHeaders(needHeaders: Boolean): Req
    fun setJsonBody(jsonBody: String): Req
    fun addHeaders(headers: Map<String, String>): Req
    fun addHeader(key: String, value: String): Req
    fun addParam(key: String, value: String): Req
    fun addParams(paramMap: Map<String, String>): Req
    fun addFormDataPart(key: String, file: File): Req
    fun addFormDataPart(map: Map<String, File>): Req

    fun connectTimeout(connectTimeout: Int): Req
    fun connectTimeout(connectTimeout: Int, timeUnit: TimeUnit): Req
    fun readTimeout(readTimeout: Int): Req
    fun readTimeout(readTimeout: Int, timeUnit: TimeUnit): Req
    fun writeTimeout(writeTimeout: Int): Req
    fun writeTimeout(writeTimeout: Int, timeUnit: TimeUnit): Req

    fun getRawCall(): Call
    fun getRequest(requestBody: RequestBody): Request
    fun getRequestBody(): RequestBody
    fun getRealUrl(): String
    fun requestBuilder(): Request.Builder
    fun enqueue(callback: RequestCallback?)
    fun execute(): HttpResponse
    fun cancel()
    fun tag(tag: Any?): Req
    fun tag(tag: String): Req


}