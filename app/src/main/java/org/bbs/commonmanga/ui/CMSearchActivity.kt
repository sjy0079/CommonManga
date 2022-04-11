package org.bbs.commonmanga.ui

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import org.bbs.commonmanga.*
import org.bbs.commonmanga.base.CMBaseActivity
import org.bbs.commonmanga.widgets.CMArrowIcon
import org.bbs.commonmanga.widgets.CMCloseIcon
import org.bbs.commonmanga.widgets.CMSearchView

class CMSearchActivity : CMBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseTitleBar.visibility = View.GONE
        baseContentView.apply {
            addView(CMSearchView(context).apply {
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, dip2px(48F)).apply {
                    topMargin = getStatusBarHeight() + dip2px(16F)
                    bottomMargin = dip2px(16F)
                    marginStart = dip2px(16F)
                    marginEnd = dip2px(48F)
                }
                postDelayed({
                    requestFocus()
                    val inputManager: InputMethodManager = context
                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.showSoftInput(this, 0)
                }, 500)
            })

            addView(CMArrowIcon(context).apply {
                layoutParams = FrameLayout.LayoutParams(dip2px(48F), dip2px(48F)).apply {
                    topMargin = getStatusBarHeight() + dip2px(16F)
                    gravity = Gravity.END
                }
                rotation = 90F
                setColorFilter(context.getColorByAttrs(R.attr.cmTextMainColor))
                setPaddingRelative(dip2px(12F), dip2px(12F), dip2px(12F), dip2px(12F))
                setOnClickListener {
                    createAnimator(0F, dip2px(20F).toFloat(), 400, {
                        translationY = it
                    }).start()
                    finishAfterTransition()
                }
                createAnimator(-dip2px(20F).toFloat(), 0F, 400, {
                    translationY = it
                }).start()
            })
        }
    }
}