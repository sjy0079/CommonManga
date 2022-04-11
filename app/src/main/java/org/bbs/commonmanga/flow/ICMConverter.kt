package org.bbs.commonmanga.flow

import org.bbs.commonmanga.viewmodel.CMMangaDetail
import org.bbs.commonmanga.viewmodel.CMSearchResult

interface ICMConverter {
    fun convertSearchResult(rawHtml: String): CMSearchResult

    fun convertDetail(rawHtml: String): CMMangaDetail
}