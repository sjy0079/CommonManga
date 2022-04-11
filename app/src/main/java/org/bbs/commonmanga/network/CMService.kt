package org.bbs.commonmanga.network

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * @author BBS
 * @since  2021-05-13
 */
interface CMService {

    @GET
    fun getMaoFlySearchResult(
        @Url url: String,
        @Query("q") keyword: String,
        @Query("page") page: Int
    ): Observable<Response<ResponseBody>>

    @GET
    fun getMaoFlyMangaDetail(@Url url: String): Observable<Response<ResponseBody>>

    @GET
    fun getMaoFlyMangaEpisode(@Url url: String): Observable<Response<ResponseBody>>
}