package com.grantgzd.kthttp.app

import com.adazhdw.kthttp.http.Net
import com.adazhdw.ktlib.Application
import com.grantgzd.kthttp.app.gson.GsonConverterFactory

/**
 * author：daguozhu
 * date-time：2020/11/13 16:57
 * description：
 **/
class LibApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }
}

val net: Net by lazy {
    Net.Builder()
        .baseUrl("https://wanandroid.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
