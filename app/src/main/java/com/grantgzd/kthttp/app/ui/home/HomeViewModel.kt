package com.grantgzd.kthttp.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adazhdw.ktlib.base.mvvm.BaseViewModelImpl
import com.adazhdw.ktlib.ext.logD
import com.adazhdw.ktlib.ext.parseAsHtml
import com.adazhdw.lasupre.*
import com.example.utiltest.sdk.net.rxjava3.subscribeAndroid
import com.grantgzd.kthttp.app.bean.DataFeed
import com.grantgzd.kthttp.app.bean.ListResponse
import com.grantgzd.kthttp.app.bean.NetResponse
import com.grantgzd.kthttp.app.bean.WxArticleChapter
import com.grantgzd.kthttp.app.lasupre
import io.reactivex.rxjava3.core.Observable
import java.io.File

class HomeViewModel : BaseViewModelImpl() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun getText() {
        val url = "wxarticle/list/408/1/json"
        /*launch {
            val time = measureTimeMillis {
                *//*val data = getRequest {
//                    get()//默认时GET
                    url("https://wanandroid.com/wxarticle/list/408/1/json")
                    queryParams("k", "Android")
                }.toClazz<NetResponse<DataFeed>>().await()*//*
                *//*val data = net.get("wxarticle/list/408/1/json")
                    .queryParams("k", "Android")
                    .parse<NetResponse<DataFeed>>().await()*//*
                val data = lasupre.get<NetResponse<DataFeed>>(urlPath = url) {
                    addQueryParam("k", "Kotlin")
                }
                val stringBuilder = StringBuilder()
                for (item in data.data.datas) {
                    stringBuilder.append("标题：${item.title.parseAsHtml()}").append("\n\n")
                }
                _text.postValue(stringBuilder.toString())
            }
            time.toString().logD("HomeViewModel")
        }*/

        lasupre.get(url)
            .addQueryParam("k", "Kotlin")
            .enqueue(object : DefaultCallback<NetResponse<DataFeed>>() {
                override fun onResponse(call: Call<NetResponse<DataFeed>>, response: Response<NetResponse<DataFeed>>) {
                    val body = response.body ?: return
                    val stringBuilder = StringBuilder()
                    for (item in body.data.datas) {
                        stringBuilder.append("标题：${item.title.parseAsHtml()}").append("\n\n")
                    }
                    _text.postValue(stringBuilder.toString())
                }
            })
        lasupre.get("wxarticle/chapters/json")
            .baseUrl("https://wanandroid.com/")
            .enqueue<ListResponse<WxArticleChapter>, Observable<ListResponse<WxArticleChapter>>>()
            .subscribeAndroid(onResult = {
                it.toString().logD(TAG)
            })
    }

    fun download() {
        val url = "https://imtt.dd.qq.com/16891/apk/06AB1F5B0A51BEFD859B2B0D6B9ED9D9.apk"
        lasupre.download("https://imtt.dd.qq.com/", "16891/apk/06AB1F5B0A51BEFD859B2B0D6B9ED9D9.apk","/KtHttp", object : ProgressListener {
            override fun onProgress(total: Long, current: Long) {
                "onComplete:---total:$total,current:$current".logD(TAG)
            }

            override fun onComplete(file: File) {
                "onComplete:${file.absolutePath}".logD(TAG)
            }

            override fun onError(throwable: Throwable) {
                "onError:$throwable".logD(TAG)
            }
        })
    }
}