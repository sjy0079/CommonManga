package org.bbs.commonmanga.viewmodel

import androidx.lifecycle.MutableLiveData
import org.bbs.commonmanga.network.CMRequests

class CMSearchResultVM : CMBaseVM() {
    private val cmSearchResult = MutableLiveData<CMSearchResult>()

    fun getSearchResult() = cmSearchResult

    fun fetchSearchResult(keyword: String, page: Int) {
        CMRequests.getSearchResult(keyword, page, cmSearchResult, getUIState())
    }
}

data class CMSearchResult(
    /**
     * page num start from 1, or invalid
     */
    var page: Int = 0,
    var hasNextPage: Boolean = false,
    var list: MutableList<CMSearchMangaInfo> = arrayListOf()
)

data class CMSearchMangaInfo(
    var id: String = String(),
    var title: String = String(),
    var author: String = String(),
    var authorId: String = String(),
    var coverUrl: String = String()
) {
    fun isValid(): Boolean {
        return id.isNotBlank() && title.isNotBlank()
    }
}