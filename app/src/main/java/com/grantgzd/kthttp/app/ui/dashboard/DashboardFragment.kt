package com.grantgzd.kthttp.app.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.adazhdw.ktlib.base.fragment.BaseFragment
import com.adazhdw.ktlib.base.mvvm.viewModel
import com.grantgzd.kthttp.app.R

class DashboardFragment(override val layoutId: Int = R.layout.fragment_dashboard) : BaseFragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel = viewModel<DashboardViewModel>()
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = ""
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView: TextView = view.findViewById(R.id.text_dashboard)
        textView.setOnClickListener {
            dashboardViewModel.getText()
            /*postRequest<NetResponse<DataFeed>>(
                url = "https://www.wanandroid.com/article/query/0/json",
                param = Param.build().addParam("k", "ViewModel"),
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
}
