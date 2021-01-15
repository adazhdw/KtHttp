package com.adazhdw.kthttp.request.base

import com.adazhdw.kthttp.entity.Param
import okhttp3.RequestBody

/**
 * author：daguozhu
 * date-time：2020/11/17 20:20
 * description：
 **/
abstract class BodyRequest(param: Param) : BaseRequest(param) {
    override fun getRequestBody(): RequestBody = param.getRequestBody()
}
