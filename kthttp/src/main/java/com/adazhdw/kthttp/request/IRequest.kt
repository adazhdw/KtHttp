package com.adazhdw.kthttp.request

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
interface IRequest {
    fun getRawCall(): Call
    fun getRequest(requestBody: RequestBody): Request
    fun getRequestBody(): RequestBody
    fun getRealUrl(): String
    fun requestBuilder(): Request.Builder
}