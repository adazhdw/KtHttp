package com.grantgzd.kthttp.app

import com.adazhdw.ktlib.Application
import com.adazhdw.ktlib.BuildConfig
import com.adazhdw.ktlib.core.delegate.DelegateExt
import com.adazhdw.lasupre.Lasupre
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

    override fun isDebug(): Boolean {
        return true
    }
}



val lasupre: Lasupre by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    Lasupre.Builder()
        .baseUrl("https://wanandroid.com/")
        .client(OkHttpClientFactory(LibApp.instance.applicationContext).create())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
}
