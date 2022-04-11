package org.bbs.commonmanga.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatEditText
import org.bbs.commonmanga.R
import org.bbs.commonmanga.dip2px
import org.bbs.commonmanga.getColorByAttrs
import org.bbs.commonmanga.getDrawableWithDayNight

class CMSearchView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AppCompatEditText(context, attributeSet, defStyleAttr) {
    companion object {
        const val TRANS_NAME = "cm_search_view_trans"
    }

    init {
        isClickable = true
        isFocusable = true
        isFocusableInTouchMode = true
        inputType = InputType.TYPE_CLASS_TEXT
        includeFontPadding = false
        background = createBackground()
        compoundDrawablePadding = dip2px(6F)
        transitionName = TRANS_NAME
        gravity = Gravity.CENTER_VERTICAL
        textSize = 18F

        setTextColor(context.getColorByAttrs(R.attr.cmTextMainColor))
        setPaddingRelative(dip2px(12F), 0, dip2px(18F), 0)
        setCompoundDrawablesRelative(
            context.getDrawableWithDayNight(R.drawable.cm_search_icon)?.apply {
                setBounds(0, dip2px(1F), intrinsicWidth, intrinsicHeight + dip2px(1F))
            },
            null, null, null
        )
    }

    private fun createBackground(): Drawable {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999F
            setStroke(dip2px(1F), context.getColorByAttrs(R.attr.cmOpacityStrokeColor))
            setColor(context.getColorByAttrs(R.attr.cmOpacityBgColor))
        }
        val mask = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999F
            setColor(context.getColor(R.color.white))
        }

        val stateList =
            ColorStateList.valueOf(context.getColorByAttrs(R.attr.cmMaskColor))
        return RippleDrawable(stateList, drawable, mask)
    }
}