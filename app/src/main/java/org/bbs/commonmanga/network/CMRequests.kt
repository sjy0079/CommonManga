package org.bbs.commonmanga.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.Disposable
import org.bbs.commonmanga.CMApplication
import org.bbs.commonmanga.R
import org.bbs.commonmanga.flow.ICMConverter
import org.bbs.commonmanga.flow.maofly.CMMaoFlyConverter
import org.bbs.commonmanga.viewmodel.CMMangaDetail
import org.bbs.commonmanga.viewmodel.CMSearchResult
import org.bbs.commonmanga.viewmodel.CMUIState

object CMRequests {
    fun getMangaDetail(
        id: String,
        liveData: MutableLiveData<CMMangaDetail>,
        uiState: MutableLiveData<CMUIState>
    ): Disposable {
        val topologyUrl = "manga/${id}.html"
        val converter: ICMConverter = CMMaoFlyConverter.instance

        return CMClient.instance.getService()
            .getMaoFlyMangaDetail(topologyUrl)
            .compose(CMTransformer.handleResult())
            .map {
                val html = it.body()?.string() ?: String()
                converter.convertDetail(html)
            }
            .subscribe({
//                Log.d(CMRequests::class.java.canonicalName, it.toString())
                it.id = id
                if (it.isValid()) {
                    liveData.value = it
                    uiState.value = CMUIState.COMPLETE
                } else {
                    uiState.value =
                        CMUIState.ERROR(CMApplication.instance.getString(R.string.cm_common_retry))
                }
            }) {
                it.printStackTrace()
                uiState.value =
                    CMUIState.ERROR(CMApplication.instance.getString(R.string.cm_common_retry))
            }
    }

    fun getSearchResult(
        keyword: String,
        page: Int,
        liveData: MutableLiveData<CMSearchResult>,
        uiState: MutableLiveData<CMUIState>
    ): Disposable {
        val topologyUrl = "search.html"
        val converter: ICMConverter = CMMaoFlyConverter.instance

        return CMClient.instance.getService()
            .getMaoFlySearchResult(topologyUrl, keyword, page)
            .compose(CMTransformer.handleResult())
            .subscribe({
                val html = it.body()?.string() ?: String()
                val res = converter.convertSearchResult(html).apply { this.page = page }
                Log.e(CMRequests::class.java.canonicalName, res.toString())
            }, {
                it.printStackTrace()
            })
    }
}