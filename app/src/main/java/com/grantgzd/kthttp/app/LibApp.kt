package com.grantgzd.kthttp.app

import com.adazhdw.kthttp.OkConfig
import com.adazhdw.kthttp.coder.UrlCoder
import com.adazhdw.ktlib.Application

/**
 * author：daguozhu
 * date-time：2020/11/13 16:57
 * description：
 **/
class LibApp : Application() {

    override fun onCreate() {
        super.onCreate()
        OkConfig.getInstance()
            .debug(isDebug())
            .needDecodeResult(false)
            .setCoder(UrlCoder.create())
    }

    override fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }
}