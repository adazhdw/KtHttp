package com.adazhdw.kthttp

import com.adazhdw.kthttp.coder.ICoder
import com.adazhdw.kthttp.coder.UrlCoder
import com.adazhdw.kthttp.constant.HttpConstant
import com.adazhdw.kthttp.converter.GsonConverter
import com.adazhdw.kthttp.converter.IConverter
import com.adazhdw.kthttp.interceptor.RetryInterceptor
import com.adazhdw.kthttp.ssl.HttpsUtils
import com.adazhdw.kthttp.util.logging.Level
import com.adazhdw.kthttp.util.logging.LoggingInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * author：daguozhu
 * date-time：2020/11/16 15:05
 * description：
 **/
object KtConfig {

    var coder: ICoder = UrlCoder.create()
    var converter: IConverter = GsonConverter.create()
    var needDecodeResult = false
    var mOkHttpClient = getOkHttpClient()
    var isDebug = false

    @JvmOverloads
    fun getOkHttpClient(timeout: Long = HttpConstant.DEFAULT_TIMEOUT): OkHttpClient {
        val sslParams = HttpsUtils.getSslSocketFactory()
        return OkHttpClient.Builder()
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .callTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor())
            .addInterceptor(RetryInterceptor())
            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager).build()
    }

    fun getLoggingInterceptor(): Interceptor {
        val level: Level = if (isDebug) Level.BODY else Level.BASIC
        return LoggingInterceptor.Builder()
            .setLevel(level).build()
    }

}