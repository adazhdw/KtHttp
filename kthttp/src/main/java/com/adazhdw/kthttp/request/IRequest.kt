package com.adazhdw.kthttp.request

import com.adazhdw.kthttp.callback.RequestCallback
import okhttp3.Call
import okhttp3.Request
import okhttp3.RequestBody

/**
 * FileName: IRequest
 * Author: adazhdw
 * Date: 2021/1/13 8:57
 * Description: Request 抽象方法提取
 * History:
 */
interface IRequest<Req : IRequest<Req>> {
    fun getRawCall(): Call
    fun getRequest(requestBody: RequestBody): Request
    fun getRequestBody(): RequestBody
    fun getRealUrl(): String
    fun requestBuilder(): Request.Builder
    fun enqueue(callback: RequestCallback?)
    fun cancel()
    fun tag(tag: Any?): Req
    fun tag(tag: String): Req
}