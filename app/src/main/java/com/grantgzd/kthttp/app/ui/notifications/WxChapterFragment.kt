package com.grantgzd.kthttp.app.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.adazhdw.kthttp.sync
import com.adazhdw.kthttp.httpRequest
import com.adazhdw.ktlib.list.ListFragment
import com.adazhdw.ktlib.list.adapter.ViewBindingAdapter
import com.adazhdw.ktlib.list.holder.BaseVBViewHolder
import com.grantgzd.kthttp.app.bean.ListResponse
import com.grantgzd.kthttp.app.bean.WxArticleChapter
import com.grantgzd.kthttp.app.databinding.NetChapterItemBinding

/**
 * author：daguozhu
 * date-time：2020/12/10 17:17
 * description：
 **/

class WxChaptersFragment : ListFragment<WxArticleChapter, ChaptersAdapter>() {
    // IM模式，最新消息在最底，通过下拉到顶部，加载历史消息
    /*override fun rvExtra(recyclerView: LoadMoreRecyclerView) {
        recyclerView.canScrollDirection(LoadMoreRecyclerView.SCROLL_DIRECTION_TOP)
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
    }*/

    override fun getDataAdapter(): ChaptersAdapter = ChaptersAdapter()

    override fun onLoad(page: Int, callback: LoadDataCallback<WxArticleChapter>) {
        val url = "https://wanandroid.com/wxarticle/chapters/json"
        /*launchOnUI {
            val data = request {//get() 默认为GET
                url(url)
            }.toClazz<ListResponse<WxArticleChapter>>().await().data ?: listOf()
            val hasmore = dataSize < 25
            callback.onSuccess(data, hasmore)
        }*/
        httpRequest {
            url(url)
        }.sync<ListResponse<WxArticleChapter>>(success = {
            val data = it.data ?: listOf()
            val hasmore = dataSize < 25
            callback.onSuccess(data, hasmore)
        })
    }
}

class ChaptersAdapter : ViewBindingAdapter<WxArticleChapter>() {

    override fun viewBinding(parent: ViewGroup, inflater: LayoutInflater, viewType: Int): ViewBinding {
        return NetChapterItemBinding.inflate(inflater, parent, false)
    }

    override fun notifyBind(holder: BaseVBViewHolder, data: WxArticleChapter, position: Int) {
        (holder.viewBinding as NetChapterItemBinding).chapterName.text = data.chapterName
    }

}



