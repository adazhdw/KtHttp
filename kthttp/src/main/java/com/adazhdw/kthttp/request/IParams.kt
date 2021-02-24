package com.adazhdw.kthttp.request

import com.adazhdw.kthttp.constant.BodyType
import com.adazhdw.kthttp.constant.Method
import java.io.File

/**
 * name：IParams
 * author：adazhdw
 * date：2021/2/23:16:32
 * description:
 */
interface IParams<Req : IParams<Req>> {

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
}