package com.grantgzd.kthttp.app.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adazhdw.ktlib.base.mvvm.BaseViewModelImpl
import com.adazhdw.ktlib.ext.logD
import com.adazhdw.ktlib.ext.parseAsHtml
import com.adazhdw.net.await
import com.grantgzd.kthttp.app.GsonUtils
import com.grantgzd.kthttp.app.bean.DataFeed
import com.grantgzd.kthttp.app.bean.NetResponse
import com.grantgzd.kthttp.app.net
import kotlin.system.measureTimeMillis

class DashboardViewModel : BaseViewModelImpl() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    fun getText() {
        launch {
            val time = measureTimeMillis {
                //"k", "ViewModel"
                val data = net.post("article/query/0/json").queryParams("k","ViewModel").parseClazz<NetResponse<DataFeed>>().await()
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