package org.bbs.commonmanga.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV

class CMStoreUtils {
    companion object {
        val instance: CMStoreUtils by lazy {
            CMStoreUtils()
        }
        private const val KEY_FAV_MANGA_PREFIX = "cm_key_fav_manga_"

        const val KEY_SITE_MAO_FLY = "site_mao_fly"
    }

    private val mmkv = MMKV.defaultMMKV()
    private val gson = Gson()
    private val strArrayListToken = object : TypeToken<ArrayList<String>>() {}

    fun isMangaInFav(site: String, id: String): Boolean = getFavMangaList(site).contains(id)

    /**
     * @return return true if the manga was already in favorite or written in mmkv
     */
    fun savFavManga(site: String, id: String): Boolean {
        val mangaList = getFavMangaList(site)
        if (mangaList.contains(id)) {
            return true
        }
        mangaList.add(id)
        return mmkv.encode(KEY_FAV_MANGA_PREFIX + site, gson.toJson(mangaList))
    }

    /**
     * @return return true if the manga was not in favorite or removed from mmkv
     */
    fun removeFavManga(site: String, id: String): Boolean {
        val mangaList = getFavMangaList(site)
        if (!mangaList.contains(id)) {
            return true
        }
        mangaList.remove(id)
        return mmkv.encode(KEY_FAV_MANGA_PREFIX + site, gson.toJson(mangaList))
    }

    fun getFavMangaList(site: String): MutableList<String> {
        val mangaListStr = mmkv.decodeString(KEY_FAV_MANGA_PREFIX + site)
        if (mangaListStr.isNullOrBlank()) {
            return arrayListOf()
        }
        return gson.fromJson(mangaListStr, strArrayListToken.type)
    }
}