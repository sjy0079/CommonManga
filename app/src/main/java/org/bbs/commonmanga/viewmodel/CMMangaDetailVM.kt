package org.bbs.commonmanga.viewmodel

import androidx.lifecycle.MutableLiveData
import org.bbs.commonmanga.network.CMRequests

class CMMangaDetailVM : CMBaseVM() {
    /**
     * live data of manga detail page
     */
    private val cmMangaDetail: MutableLiveData<CMMangaDetail> = MutableLiveData()

    fun getDetail() = cmMangaDetail

    fun fetchDetail(id: String) {
        getUIState().value = CMUIState.LOADING
        CMRequests.getMangaDetail(id, cmMangaDetail, getUIState())
    }
}

data class CMMangaDetail(
    var id: String = String(),
    var title: String = String(),
    /**
     * the manga's title in its origin lang
     */
    var originTitle: String = String(),
    var coverUrl: String = String(),
    var author: String = String(),
    var updateTime: String = String(),
    var status: String = String(),
    var nowReadingSectionName: String = String(),
    var nowReadingEpName: String = String(),
    var sectionList: MutableList<CMMangaSection>? = null
) {
    companion object {
        const val KEY_CM_MANGA_ID = "common_manga_manga_id"
    }

    /**
     * if the data is valid to show
     */
    fun isValid(): Boolean {
        return id.isNotBlank() && (title.isNotBlank() || originTitle.isNotBlank())
    }
}

data class CMMangaSection(
    var sectionName: String = String(),
    var epList: MutableList<CMMangaEpPreview> = arrayListOf()
) {
    fun isValid(): Boolean {
        return sectionName.isNotBlank()
    }
}

data class CMMangaEpPreview(
    var epName: String = String(),
    var epUrl: String = String()
) {
    fun isValid(): Boolean {
        return epName.isNotBlank() && epUrl.isNotBlank()
    }
}