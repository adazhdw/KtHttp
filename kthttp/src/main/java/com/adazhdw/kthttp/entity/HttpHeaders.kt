package com.adazhdw.kthttp.entity

import com.adazhdw.kthttp.OkConfig

/**
 * author：daguozhu
 * date-time：2020/11/17 16:40
 * description：请求头封装
 **/
class HttpHeaders : MapEntity<String>() {
    /** 请求头存放集合 */
    init {
        contents.putAll(OkConfig.config.getCommonHeaders())
    }

}