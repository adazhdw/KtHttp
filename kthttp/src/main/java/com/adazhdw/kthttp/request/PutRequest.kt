package com.adazhdw.kthttp.request

import com.adazhdw.kthttp.entity.Param
import com.adazhdw.kthttp.request.base.BodyRequest
import okhttp3.Request
import okhttp3.RequestBody

/**
 * author：daguozhu
 * date-time：2020/9/3 17:21
 * description：
 **/
class PutRequest(param: Param) : BodyRequest(param) {

    override fun getRequest(requestBody: RequestBody): Request {
        return requestBuilder().put(requestBody).url(param.url).tag(tag).build()
    }

}