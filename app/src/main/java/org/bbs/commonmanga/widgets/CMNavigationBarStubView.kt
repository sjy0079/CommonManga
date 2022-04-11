package org.bbs.commonmanga.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import org.bbs.commonmanga.getNavigationBarHeight

class CMNavigationBarStubView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        try {
            params?.height = getNavigationBarHeight()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.setLayoutParams(params)
    }
}