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
import okhttp3.Headers
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
    private val mParams: HashMap<String, String> = hashMapOf()
    private val mHeaders: HashMap<String, String> = hashMapOf()


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

    /**
     * 设置 公共 header 参数
     */
    fun addCommonHeaders(headers: Map<String, String>) {
        mHeaders.putAll(headers)
    }

    /**
     * 获取 公共 header 参数
     */
    fun getCommonHeaders(): HashMap<String, String> {
        return mHeaders
    }

    /**
     * 获取 公共 header 参数
     */
    fun getCommonHttpHeaders(): Headers {
        val headers = Headers.Builder()
        for ((name, value) in mHeaders) {
            headers.add(name, value)
        }
        return headers.build()
    }

    /**
     * 设置 公共参数
     */
    fun setCommonParams(params: Map<String, String>) {
        mParams.putAll(params)
    }

    /**
     * 获取 公共参数
     */
    fun getCommonParams(): HashMap<String, String> {
        return mParams
    }

}