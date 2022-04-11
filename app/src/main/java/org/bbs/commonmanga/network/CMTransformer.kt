package org.bbs.commonmanga.network

import com.google.gson.JsonElement
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * @author BBS
 * @since  2021-05-13
 */
class CMTransformer {
    companion object {
        /**
         * switch thread & check err code
         */
        fun <T> handleResult(): ObservableTransformer<T, T> {
            return ObservableTransformer { upstream ->
                upstream
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
        }
    }
}