package com.grantgzd.kthttp.app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import com.adazhdw.kthttp.http.Call
import com.adazhdw.kthttp.http.Callback
import com.adazhdw.kthttp.http.Response
import com.adazhdw.ktlib.base.fragment.BaseFragment
import com.adazhdw.ktlib.base.mvvm.viewModel
import com.adazhdw.ktlib.ext.parseAsHtml
import com.grantgzd.kthttp.app.R
import com.grantgzd.kthttp.app.bean.DataFeed
import com.grantgzd.kthttp.app.bean.NetResponse
import com.grantgzd.kthttp.app.net

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
            textView.text = ""
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView: TextView = view.findViewById(R.id.text_home)
        val request: Button = view.findViewById(R.id.request)

        textView.setOnClickListener {
            homeViewModel.getText()
        }
        request.setOnClickListener {
            requestText()
        }
    }

    private fun requestText() {
        val textView: TextView = view?.findViewById(R.id.text_home) ?: return
        net.get().urlPath("wxarticle/list/408/1/json").queryParams("k", "Android")
            .parseCall<NetResponse<DataFeed>>()
            .enqueue(object : Callback<NetResponse<DataFeed>> {
                override fun onResponse(call: Call<NetResponse<DataFeed>>, response: Response<NetResponse<DataFeed>>) {
                    val stringBuilder = StringBuilder()
                    for (item in response.body!!.data.datas) {
                        stringBuilder.append("标题：${item.title.parseAsHtml()}").append("\n\n")
                    }
                    textView.text = stringBuilder.toString()
                }

                override fun onFailure(call: Call<NetResponse<DataFeed>>, t: Throwable) {
                    Log.d(TAG, "t:$t,call:$call")
                }
            })
    }

    override val layoutId: Int
        get() = R.layout.fragment_home
}
