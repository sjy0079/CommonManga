package org.bbs.commonmanga

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

/**
 * status bar height in pixel
 */
fun getStatusBarHeight(): Int {
    val resources: Resources = CMApplication.instance.resources
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

/**
 * navigation bar height in pixel
 */
fun getNavigationBarHeight(): Int {
    val resources: Resources = CMApplication.instance.resources
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

/**
 * get color id by attrs
 */
fun Context.getColorByAttrs(@AttrRes attrId: Int): Int {
    return ContextCompat.getColor(this, getResByAttrs(attrId))
}

/**
 * get restored res id by attrs
 */
fun Context.getResByAttrs(attrId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrId, typedValue, false)
    return typedValue.data
}

/**
 * set color filter with opacity
 */
fun ImageView.setColorAlphaFilter(color: Int) {
    if (Color.alpha(color) == 0xFF) {
        setColorFilter(color)
    } else {
        setColorFilter(Color.rgb(Color.red(color), Color.green(color), Color.blue(color)))
        alpha = Color.alpha(color).toFloat() / 0xFF
    }
}

/**
 * convert dp to px
 */
fun dip2px(dip: Float): Int {
    val f = CMApplication.instance.resources.displayMetrics.density
    return (dip * f + 0.5F).toInt()
}

/**
 * get screen width in dp
 */
fun Context.getScreenWidthDp(): Int =
    resources.displayMetrics.run { widthPixels / density }.toInt()

/**
 * load network image by glide
 */
fun ImageView.loadUrl(url: String?) {
    if (url.isNullOrBlank()) {
        return
    }
    Glide.with(CMApplication.instance)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

/**
 * get drawable and set color filter by day night theme
 */
fun Context.getDrawableWithDayNight(id: Int): Drawable? =
    AppCompatResources.getDrawable(this, id)?.also {
        val color = getColorByAttrs(R.attr.colorOnSecondary)
        it.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

/**
 * get drawable and set color filter by color
 */
fun Context.getDrawableWithColor(id: Int, color: Int): Drawable? =
    AppCompatResources.getDrawable(this, id)?.also {
        it.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

/**
 * get an animator conveniently
 */
@SuppressLint("Recycle")
@Suppress("UNCHECKED_CAST")
fun <T : Any> createAnimator(
    from: T,
    to: T,
    time: Long,
    onUpdate: (T) -> Unit,
    onStart: ((Animator) -> Unit)? = null,
    onEnd: ((Animator) -> Unit)? = null,
    interpolator: TimeInterpolator? = AccelerateInterpolator()
): ValueAnimator {
    val animator =
        when (from::class) {
            Int::class -> {
                ValueAnimator.ofInt(from as Int, to as Int)
            }
            Float::class -> {
                ValueAnimator.ofFloat(from as Float, to as Float)
            }
            else -> {
                throw Exception("Not support type ${from::class.java.canonicalName}")
            }
        }
    return animator.apply {
        duration = time
        this.interpolator = interpolator
        addUpdateListener {
            onUpdate.invoke(it.animatedValue as T)
        }
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                onStart?.invoke(animation)
            }

            override fun onAnimationEnd(animation: Animator) {
                onEnd?.invoke(animation)
            }

            override fun onAnimationCancel(animation: Animator) {
                onEnd?.invoke(animation)
            }
        })
    }
}