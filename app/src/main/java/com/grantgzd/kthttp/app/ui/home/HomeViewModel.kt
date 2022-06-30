package com.grantgzd.kthttp.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adazhdw.ktlib.base.mvvm.BaseViewModelImpl
import com.adazhdw.ktlib.ext.logD
import com.adazhdw.ktlib.ext.parseAsHtml
import com.adazhdw.lasupre.get
import com.grantgzd.kthttp.app.bean.DataFeed
import com.grantgzd.kthttp.app.bean.NetResponse
import com.grantgzd.kthttp.app.lasupre
import kotlin.system.measureTimeMillis

class HomeViewModel : BaseViewModelImpl() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun getText() {
        launch {
            val time = measureTimeMillis {
                /*val data = getRequest {
//                    get()//默认时GET
                    url("https://wanandroid.com/wxarticle/list/408/1/json")
                    queryParams("k", "Android")
                }.toClazz<NetResponse<DataFeed>>().await()*/
                /*val data = net.get("wxarticle/list/408/1/json")
                    .queryParams("k", "Android")
                    .parse<NetResponse<DataFeed>>().await()*/
                val data = lasupre.get<NetResponse<DataFeed>>(urlPath = "wxarticle/list/408/1/json"){
                    queryParams("k", "Android")
                }
                val stringBuilder = StringBuilder()
                for (item in data.data.datas) {
                    stringBuilder.append("标题：${item.title.parseAsHtml()}").append("\n\n")
                }
                _text.postValue(stringBuilder.toString())
            }
            time.toString().logD("HomeViewModel")
        }
    }
}