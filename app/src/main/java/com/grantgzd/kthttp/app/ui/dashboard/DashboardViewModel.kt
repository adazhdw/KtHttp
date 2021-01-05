package com.grantgzd.kthttp.app.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adazhdw.kthttp.KtHttp
import com.adazhdw.kthttp.coroutines.toClazz
import com.adazhdw.kthttp.entity.Param
import com.adazhdw.kthttp.ext.get
import com.adazhdw.kthttp.ext.post
import com.adazhdw.kthttp.ext.request
import com.adazhdw.ktlib.base.mvvm.BaseViewModelImpl
import com.adazhdw.ktlib.ext.logD
import com.adazhdw.ktlib.ext.parseAsHtml
import com.grantgzd.kthttp.app.bean.DataFeed
import com.grantgzd.kthttp.app.bean.NetResponse
import kotlin.system.measureTimeMillis

class DashboardViewModel : BaseViewModelImpl() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    fun getText() {
        launch {
            val time = measureTimeMillis {
                val data = request {
                    post()
                    url("https://www.wanandroid.com/article/query/0/json")
                    addParam("k", "ViewModel")
                }.toClazz<NetResponse<DataFeed>>().await()
                val stringBuilder = StringBuilder()
                for (item in data.data.datas) {
                    stringBuilder.append("标题：${item.title.parseAsHtml()}").append("\n\n")
                }
                _text.postValue(stringBuilder.toString())
            }
            time.toString().logD("DashboardViewModel")
        }
    }
}