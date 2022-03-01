package com.grantgzd.kthttp.app

import com.adazhdw.ktlib.Application
import com.adazhdw.ktlib.core.delegate.DelegateExt
import com.adazhdw.net.Net
import com.grantgzd.kthttp.app.net.CoroutineCallAdapterFactory
import com.grantgzd.kthttp.app.net.GsonConverterFactory
import com.grantgzd.kthttp.app.net.OkHttpClientFactory

/**
 * author：daguozhu
 * date-time：2020/11/13 16:57
 * description：
 **/
class LibApp : Application() {

    companion object{

        var instance: LibApp by DelegateExt.notNullSingleValue()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}


val net: Net by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    Net.Builder()
        .baseUrl("https://wanandroid.com/")
        .client(OkHttpClientFactory(LibApp.instance.applicationContext).create())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
}

