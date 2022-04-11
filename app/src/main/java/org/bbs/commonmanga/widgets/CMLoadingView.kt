package org.bbs.commonmanga.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.progressindicator.CircularProgressIndicator
import org.bbs.commonmanga.R
import org.bbs.commonmanga.createAnimator
import org.bbs.commonmanga.dip2px
import org.bbs.commonmanga.getColorByAttrs

class CMLoadingView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {
    private val progressBar = CircularProgressIndicator(context).apply {
        layoutParams = LayoutParams(dip2px(40F), dip2px(40F)).apply {
            gravity = Gravity.CENTER
        }
        isIndeterminate = true
    }
    private val loadingInfoText = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        }
        textSize = 18F
        setText(R.string.cm_common_retry)
        setTextColor(context.getColorByAttrs(R.attr.cmTextMainColor))
    }

    init {
        addView(progressBar)
        addView(loadingInfoText)
    }

    fun setRefreshListener(listener: OnClickListener) {
        loadingInfoText.setOnClickListener(listener)
    }

    fun showLoading() {
        progressBar.visibility = View.VISIBLE
        loadingInfoText.visibility = View.GONE
    }

    fun showFailPage(info: String = String()) {
        progressBar.visibility = View.GONE
        loadingInfoText.apply {
            info.takeIf { it.isNotBlank() }?.let {
                text = it
            }
            visibility = View.VISIBLE
        }
    }

    fun superSetVisibility(visibility: Int) {
        super.setVisibility(visibility)
    }

    override fun setVisibility(visibility: Int) {
        when (visibility) {
            View.VISIBLE -> {
                createAnimator(0F, 1F, 200L, { alpha ->
                    this.alpha = alpha
                }, {
                    this.alpha = 0F
                    super.setVisibility(visibility)
                }).start()
            }
            View.GONE -> {
                createAnimator(1F, 0F, 200L, { alpha ->
                    this.alpha = alpha
                }, onEnd = {
                    super.setVisibility(visibility)
                }).start()
            }
            else -> {
                super.setVisibility(visibility)
            }
        }
    }
}