package org.bbs.commonmanga.base

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import org.bbs.commonmanga.network.OkHttpUrlLoader
import org.bbs.commonmanga.network.UnsafeOkHttpClient.unsafeOkHttpClient
import java.io.InputStream

@GlideModule
class CMGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val okHttpClient = unsafeOkHttpClient.build()
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(okHttpClient)
        )
    }
}