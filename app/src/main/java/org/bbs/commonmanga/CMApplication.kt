package org.bbs.commonmanga

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.tencent.mmkv.MMKV
import org.bbs.commonmanga.network.CMClient

class CMApplication : Application(), ViewModelStoreOwner {
    private lateinit var vmStore: ViewModelStore

    companion object {
        lateinit var instance: CMApplication
            private set
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        instance = this
        vmStore = ViewModelStore()

        CMClient.instance.init()
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }

    override fun getViewModelStore() = vmStore
}