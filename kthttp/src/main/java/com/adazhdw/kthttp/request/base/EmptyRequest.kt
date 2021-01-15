package com.adazhdw.kthttp.request.base

import com.adazhdw.kthttp.entity.Param
import com.adazhdw.kthttp.util.RequestUrlUtil
import okhttp3.RequestBody
import okhttp3.internal.EMPTY_REQUEST

/**
 * author：daguozhu
 * date-time：2020/9/3 16:25
 * description：
 **/
abstract class EmptyRequest(param: Param) : BaseRequest(param) {

    final override fun getRequestBody(): RequestBody = EMPTY_REQUEST

    override fun getRealUrl(): String {
        val commonParams = param.params()
        return RequestUrlUtil.getFullUrl2(param.url, commonParams, param.urlEncoder)
    }
}