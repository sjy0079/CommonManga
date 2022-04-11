package org.bbs.commonmanga.widgets

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import org.bbs.commonmanga.R
import org.bbs.commonmanga.setColorAlphaFilter

/**
 * 滴滴国际化司机端图标合集
 *
 * @author shenjiayi@didiglobal.com
 * @since  2020/6/15
 */
abstract class CMBaseIcon @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AppCompatImageView(context, attributeSet, defStyleAttr) {

    init {
        super.setBackgroundResource(R.drawable.cm_base_icon_bg)
        super.setImageResource(this.getImageResource())
        // rippleDrawable默认触发点击效果，所以先使其不可用（仅展示）
        super.setEnabled(false)

        val a = context.obtainStyledAttributes(attributeSet, R.styleable.CMBaseIcon)
        var color = a.getColor(R.styleable.CMBaseIcon_cmIconColor, 0)
        if (color == 0) {
            try {
                val colorStr = a.getString(R.styleable.CMBaseIcon_cmIconColor)
                if (!colorStr.isNullOrBlank()) {
                    color = Color.parseColor(colorStr)
                }
            } catch (ignore: Throwable) {
            }
        }
        if (color != 0) {
            setColorAlphaFilter(color)
        }
        a.recycle()
    }

    /**
     * 设置过点击监听后，将其恢复成可用状态
     */
    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        isEnabled = true
    }

    /**
     * 获取具体的图片资源
     */
    @DrawableRes
    abstract fun getImageResource(): Int
}

/**
 * 关闭图标（×）
 */
class CMCloseIcon @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    CMBaseIcon(context, attributeSet, defStyleAttr) {

    override fun getImageResource(): Int = R.drawable.cm_icon_titlebar_close
}

/**
 * 不带柄的箭头（>）
 */
class CMArrowIcon @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    CMBaseIcon(context, attributeSet, defStyleAttr) {

    override fun getImageResource(): Int = R.drawable.cm_icon_arrow
}

/**
 * 带柄的箭头（->）
 */
class CMLongArrowIcon @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    CMBaseIcon(context, attributeSet, defStyleAttr) {

    override fun getImageResource(): Int = R.drawable.cm_icon_long_arrow
}

/**
 * 填充圆形、镂空感叹号的警示图标
 */
class CMSolidPromptIcon @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    CMBaseIcon(context, attributeSet, defStyleAttr) {

    override fun getImageResource(): Int = R.drawable.cm_icon_prompt_caveat
}

/**
 * 填充通知铃铛图标
 */
class CMSolidRingIcon @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    CMBaseIcon(context, attributeSet, defStyleAttr) {

    override fun getImageResource(): Int = R.drawable.cm_icon_prompt_ring
}