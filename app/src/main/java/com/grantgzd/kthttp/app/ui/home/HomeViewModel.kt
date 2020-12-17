package com.adazhdw.libapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adazhdw.kthttp.KtHttp
import com.adazhdw.kthttp.coroutines.toClazz
import com.adazhdw.kthttp.entity.Param
import com.adazhdw.ktlib.base.mvvm.BaseViewModelImpl
import com.adazhdw.ktlib.ext.logD
import com.adazhdw.ktlib.ext.parseAsHtml
import com.grantgzd.kthttp.app.bean.DataFeed
import com.grantgzd.kthttp.app.bean.NetResponse
import kotlin.system.measureTimeMillis

class HomeViewModel : BaseViewModelImpl() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun getText() {
        launch {
            val time = measureTimeMillis {
                val data = KtHttp.ktHttp.get(
                    url = "https://wanandroid.com/wxarticle/list/408/1/json",
                    param = Param.build().addParam("k", "Android")
                ).toClazz<NetResponse<DataFeed>>().await()
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