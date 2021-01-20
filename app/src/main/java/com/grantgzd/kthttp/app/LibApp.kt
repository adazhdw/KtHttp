package com.grantgzd.kthttp.app

import com.adazhdw.kthttp.OkExt
import com.adazhdw.ktlib.Application

/**
 * author：daguozhu
 * date-time：2020/11/13 16:57
 * description：
 **/
class LibApp : Application() {

    override fun onCreate() {
        super.onCreate()
        OkExt.isDebug = isDebug()
    }

    override fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }
}