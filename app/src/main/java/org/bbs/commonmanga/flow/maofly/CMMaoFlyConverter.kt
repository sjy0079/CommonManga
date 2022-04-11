package org.bbs.commonmanga.flow.maofly

import org.bbs.commonmanga.flow.ICMConverter
import org.bbs.commonmanga.viewmodel.*
import org.jsoup.Jsoup

class CMMaoFlyConverter private constructor() : ICMConverter {
    companion object {
        val instance: CMMaoFlyConverter by lazy {
            CMMaoFlyConverter()
        }
    }

    override fun convertSearchResult(rawHtml: String): CMSearchResult {
        val doc = Jsoup.parse(rawHtml)
        val result = CMSearchResult()
        // find list
        val resultElements = doc.select("div.comicbook-index.mb-2.mb-sm-0")
        resultElements.forEach { elem ->
            val titleElem = elem.select("h2.mt-0.mb-1.one-line").first()
            val mangaHref = titleElem?.getElementsByTag("a")?.first()
            var idStr: String? = null
            mangaHref?.attr("href")?.let { url ->
                idStr = Regex(pattern = """\d+""").find(url)?.value
            }
            val titleStr = mangaHref?.text()
            val authorElem =
                elem.getElementsByClass("comic-author").first()?.getElementsByTag("a")?.first()
            val authorStr = authorElem?.text()
            var authorIdStr: String? = null
            authorElem?.attr("href")?.let { url ->
                authorIdStr = Regex(pattern = """\d+""").find(url)?.value
            }
            val coverUrlStr = elem.getElementsByTag("img").first()?.attr("src")
            val mangaInfo = CMSearchMangaInfo().apply {
                idStr?.let { id = it }
                titleStr?.let { title = it }
                authorStr?.let { author = it }
                authorIdStr?.let { authorId = it }
                coverUrlStr?.let { coverUrl = it }
            }
            mangaInfo.takeIf { it.isValid() }?.let { result.list.add(it) }
        }
        if (result.list.isEmpty()) {
            return result
        }
        val pageElem = doc.select("div.pagination")
        // case 1: no other page
        if (pageElem.isEmpty()) {
            return result
        }
        // case 2: has next page button
        val pageItems = pageElem.first()?.getElementsByClass("page-item")
        pageItems?.forEach {
            if (it.text().contains("下一页")) {
                result.hasNextPage = true
            }
        }
        return result
    }

    override fun convertDetail(rawHtml: String): CMMangaDetail {
        val doc = Jsoup.parse(rawHtml)
        val result = CMMangaDetail()
        val mangaDataELem =
            doc.body().getElementsByClass("comic-meta-data-table").first() ?: return result
        // find title
        val titleStr =
            mangaDataELem.getElementsByClass("comic-titles").first()?.text()
        val titleOriginStr =
            mangaDataELem.getElementsByClass("sub-title").first()?.text()
        // find cover
        val coverUrlStr =
            mangaDataELem.getElementsByClass("comic-cover").first()?.getElementsByTag("img")
                ?.first()?.attr("src")
        // find author
        val authorStr =
            mangaDataELem.getElementsByClass("pub-duration").first()?.getElementsByTag("a")?.first()
                ?.text()
        // find update time
        val updateTimeStr = mangaDataELem.getElementsByTag("td").last()?.text()
        // find status
        var statusStr: String? = null
        val trList = mangaDataELem.getElementsByTag("tr")
        trList.forEach {
            val th = it.getElementsByTag("th").first()
            if (th != null && th.text() == "发行状态") {
                statusStr = it.getElementsByTag("td").first()?.text()
            }
        }
        // find sections & eps
        val sectionElem =
            doc.getElementById("comic-book-list")?.select("div.tab-pane.fade.show.active")
                ?: listOf()
        val sectionListTemp = arrayListOf<CMMangaSection>()
        sectionElem.forEach {
            val section = CMMangaSection()
            section.apply {
                sectionName =
                    it.getElementsByClass("comic_version_title").first()?.text() ?: String()
                epList = arrayListOf()
            }
            (it.getElementsByClass("fixed-a-es") ?: listOf()).forEach { ep ->
                val epPreview = CMMangaEpPreview().apply {
                    epName = ep.text()
                    epUrl = ep.attr("href")
                }
                section.epList.add(epPreview)
            }
            sectionListTemp.add(section)
        }
        result.apply {
            titleOriginStr?.let { originTitle = it }
            titleStr?.let { title = it.replace(originTitle, String()) }
            coverUrlStr?.let { coverUrl = it }
            authorStr?.let { author = it }
            updateTimeStr?.let { updateTime = it }
            statusStr?.let { status = it }
            sectionList = sectionListTemp
        }
        return result
    }
}