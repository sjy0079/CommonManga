package org.bbs.commonmanga.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.bbs.commonmanga.utils.CMStoreUtils

class CMMangaFavVM : ViewModel() {

    private val favListMap = HashMap<String, MutableLiveData<MutableList<String>>>()

    fun getFavList(site: String): MutableLiveData<MutableList<String>> {
        ensureData(site)
        return favListMap[site]!!
    }

    fun fav(site: String, id: String) {
        ensureData(site)
        if (CMStoreUtils.instance.isMangaInFav(site, id)) {
            return
        }
        if (CMStoreUtils.instance.savFavManga(site, id)) {
            favListMap[site]!!.value = CMStoreUtils.instance.getFavMangaList(site)
        }
    }

    fun dislike(site: String, id: String) {
        ensureData(site)
        if (!CMStoreUtils.instance.isMangaInFav(site, id)) {
            return
        }
        if (CMStoreUtils.instance.removeFavManga(site, id)) {
            favListMap[site]!!.value = CMStoreUtils.instance.getFavMangaList(site)
        }
    }

    private fun ensureData(site: String) {
        if (favListMap[site] == null) {
            MutableLiveData<MutableList<String>>().also {
                it.value = CMStoreUtils.instance.getFavMangaList(site)
                favListMap[site] = it
            }
        }
    }
}