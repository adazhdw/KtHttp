package com.grantgzd.kthttp.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import com.adazhdw.ktlib.base.fragment.BaseFragment
import com.adazhdw.ktlib.base.mvvm.viewModel
import com.grantgzd.kthttp.app.R

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

    }

    override val layoutId: Int
        get() = R.layout.fragment_home
}
