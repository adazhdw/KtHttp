package com.adazhdw.kthttp

import com.adazhdw.kthttp.coder.ICoder
import com.adazhdw.kthttp.coder.UrlCoder
import com.adazhdw.kthttp.constant.HttpConstant
import com.adazhdw.kthttp.converter.GsonConverter
import com.adazhdw.kthttp.converter.IConverter
import com.adazhdw.kthttp.interceptor.RetryInterceptor
import com.adazhdw.kthttp.ssl.SSLUtils
import com.adazhdw.kthttp.util.logging.Level
import com.adazhdw.kthttp.util.logging.LoggingInterceptor
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * name：OkConfig
 * author：adazhdw
 * date：2021/2/24:9:40
 * description:
 */
class OkConfig private constructor() {

    companion object {
        val config by lazy { OkConfig() }
    }

    var coder: ICoder = UrlCoder.create()
        private set
    var converter: IConverter = GsonConverter.create()
        private set
    var needDecodeResult = false
        private set
    var mOkHttpClient = getOkHttpClient()
        private set
    var isDebug = false
        private set
    private val mParams: HashMap<String, String> = hashMapOf()
    private val mHeaders: HashMap<String, String> = hashMapOf()

    @JvmOverloads
    fun getOkHttpClient(timeout: Long = HttpConstant.DEFAULT_TIMEOUT): OkHttpClient {
        val sslParams = SSLUtils.getSslSocketFactory()
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
     * 获取 公共参数
     */
    fun getCommonParams(): HashMap<String, String> {
        return mParams
    }

    /**
     * 设置 编码工具类
     */
    fun setCoder(coder: ICoder) = apply {
        this.coder = coder
    }

    /**
     * 设置 json 转换类
     */
    fun setConverter(converter: IConverter) = apply {
        this.converter = converter
    }

    /**
     * 设置是否需要 decode 返回结果数据
     */
    fun needDecodeResult(needDecodeResult: Boolean) = apply {
        this.needDecodeResult = needDecodeResult
    }

    /**
     * 设置 okhttpclient
     */
    fun setClient(client: OkHttpClient) = apply {
        this.mOkHttpClient = client
    }

    /**
     * 设置 debug 模式
     */
    fun debug(debug: Boolean) = apply {
        this.isDebug = debug
    }

    /**
     * 设置 公共 header 参数
     */
    fun addCommonHeaders(headers: Map<String, String>) = apply {
        mHeaders.putAll(headers)
    }

    /**
     * 设置 公共参数
     */
    fun setCommonParams(params: Map<String, String>) = apply {
        mParams.putAll(params)
    }


}