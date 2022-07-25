package com.grantgzd.kthttp.app

import android.content.Context
import androidx.multidex.MultiDex
import com.adazhdw.ktlib.Application
import com.adazhdw.ktlib.core.delegate.DelegateExt
import com.adazhdw.lasupre.Lasupre
import com.grantgzd.kthttp.app.net.CoroutineCallAdapterFactory
import com.grantgzd.kthttp.app.net.OkHttpClientFactory
import lasupre.adapter.rxjava3.RxJava3CallAdapterFactory
import lasupre.converter.gson.GsonConverterFactory

/**
 * author：daguozhu
 * date-time：2020/11/13 16:57
 * description：
 **/
class LibApp : Application() {

    companion object {
        var instance: LibApp by DelegateExt.notNullSingleValue()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
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
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
}
