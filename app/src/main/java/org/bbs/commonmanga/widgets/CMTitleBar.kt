@file:Suppress("unused")

package org.bbs.commonmanga.widgets

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import org.bbs.commonmanga.R
import org.bbs.commonmanga.databinding.CmTitleLayoutBinding
import org.bbs.commonmanga.dip2px
import org.bbs.commonmanga.getColorByAttrs
import org.bbs.commonmanga.getStatusBarHeight

class CMTitleBar @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private val binding = CmTitleLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    private val titleBg: Drawable

    private var isBgTransparent: Boolean = false

    init {
        binding.backBtn.setOnClickListener {
            if (context is Activity) {
                context.finish()
            }
        }
        titleBg = ColorDrawable(context.getColorByAttrs(R.attr.colorOnPrimary))
        binding.titleContainer.apply {
            background = titleBg
            setPaddingRelative(0, getStatusBarHeight(), 0, 0)
        }
        resetStatus()
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        super.setLayoutParams(params.apply {
            if (this is MarginLayoutParams) {
                bottomMargin = -dip2px(6F)
            }
        })
    }

    fun setTitle(title: CharSequence) {
        binding.titleTv.text = title
    }

    fun setBackButtonVisible(visible: Boolean) {
        if (visible) {
            binding.backBtn.visibility = View.VISIBLE
        } else {
            binding.backBtn.visibility = View.GONE
        }
    }

    /**
     * set visibility with status bar stub height
     */
    fun setVisibilityWithStubShow(visible: Boolean) {
        if (visible) {
            binding.titleContainer.visibility = View.VISIBLE
            if (isBgTransparent) {
                binding.titleShadow.visibility = View.INVISIBLE
            } else {
                binding.titleShadow.visibility = View.VISIBLE
            }
        } else {
            binding.titleContainer.visibility = View.GONE
        }
    }

    fun setBgAlpha(alpha: Float) {
        titleBg.alpha = (alpha * 255).toInt()
        binding.titleShadow.alpha = alpha
    }

    fun setBgTransparent(isTransparent: Boolean) {
        if (isTransparent) {
            binding.apply {
                root.background = null
                titleShadow.visibility = View.INVISIBLE
            }
        } else {
            binding.apply {
                root.background = titleBg
                titleShadow.visibility = View.VISIBLE
            }
        }
        isBgTransparent = isTransparent
    }

    fun setBgColor(@ColorInt color: Int) {
        val titleAlpha = titleBg.alpha

        (titleBg.mutate() as ColorDrawable).apply {
            this.color = color
            this.alpha = titleAlpha
        }
    }

    fun setTextColor(@ColorInt color: Int) {
        binding.apply {
            titleTv.setTextColor(color)
            backBtn.setColorFilter(color)
        }
    }

    private fun resetStatus() {
        titleBg.alpha = 255
        binding.apply {
            titleContainer.background = titleBg
            titleShadow.apply {
                alpha = 1F
                visibility = View.VISIBLE
            }
        }
        isBgTransparent = false
    }
}