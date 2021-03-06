package com.adazhdw.kthttp.internal.callback

import androidx.lifecycle.LifecycleOwner
import com.adazhdw.kthttp.HttpClient
import okhttp3.Call
import okhttp3.Response

/**
 * author：daguozhu
 * date-time：2020/9/5 16:09
 * description：
 **/
interface RequestCallback {
    val mLifecycleOwner: LifecycleOwner?
    val httpClient: HttpClient

    /** 请求网络开始前，UI线程 */
    fun onStart(call: Call)

    /** 对返回数据进行操作的回调， UI线程 */
    fun onResult(response: Response, call: Call)

    /** 请求失败，响应错误，数据解析错误等，都会回调该方法， UI线程 */
    fun onFailure(e: Exception, call: Call)

    /** 请求网络结束后，UI线程 */
    fun onFinish()
}
