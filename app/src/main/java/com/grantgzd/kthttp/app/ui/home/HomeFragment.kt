package com.grantgzd.kthttp.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.adazhdw.kthttp.ext.execute
import com.adazhdw.kthttp.ext.httpRequest
import com.adazhdw.ktlib.base.fragment.BaseFragment
import com.adazhdw.ktlib.base.mvvm.viewModel
import com.adazhdw.ktlib.ext.parseAsHtml
import com.grantgzd.kthttp.app.R
import com.grantgzd.kthttp.app.bean.DataFeed
import com.grantgzd.kthttp.app.bean.NetResponse

class HomeFragment : BaseFragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = viewModel<HomeViewModel>()
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView: TextView = view.findViewById(R.id.text_home)

//        homeViewModel.getText()
        textView.setOnClickListener {

            httpRequest {
                url("https://wanandroid.com/wxarticle/list/408/1/json")
                addParam("k", "Android")
            }.execute<NetResponse<DataFeed>>(lifecycleOwner = this, success = { data ->
                val stringBuilder = StringBuilder()
                for (item in data.data.datas) {
                    stringBuilder.append("标题：${item.title.parseAsHtml()}").append("\n\n")
                }
                textView.text = stringBuilder.toString()
            })
            /*homeViewModel.getText()
            getRequest<NetResponse<DataFeed>>(
                url = "https://wanandroid.com/wxarticle/list/408/1/json",
                param = Param.build().addParam("k", "Android"),
                success = { data ->
                    val stringBuilder = StringBuilder()
                    for (item in data.data.datas) {
                        stringBuilder.append("标题：${item.title}").append("\n\n")
                    }
                    textView.text = stringBuilder.toString()
                }, error = { code, msg ->

                })*/
        }
    }

    override val layoutId: Int
        get() = R.layout.fragment_home
}
