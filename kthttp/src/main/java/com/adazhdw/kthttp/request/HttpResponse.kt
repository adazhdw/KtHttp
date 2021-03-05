package com.adazhdw.kthttp.request

/**
 * name：HttpResponse
 * author：adazhdw
 * date：2021/3/5:16:48
 * description:
 */
class HttpResponse(val rawResponse: okhttp3.Response) {
    val message: String = rawResponse.message
    val code: Int = rawResponse.code
    val succeed: Boolean = rawResponse.isSuccessful
}