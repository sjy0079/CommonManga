package org.bbs.commonmanga.base

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.transition.Fade
import android.transition.TransitionInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import org.bbs.commonmanga.R
import org.bbs.commonmanga.widgets.CMLoadingView
import org.bbs.commonmanga.widgets.CMTitleBar

@Suppress("unused")
abstract class CMBaseActivity : AppCompatActivity() {
    private val savedInstanceStateMap = HashMap<String, Any>()
    lateinit var baseContentView: FrameLayout
        private set
    lateinit var baseTitleBar: CMTitleBar
        private set
    lateinit var baseLoadingView: CMLoadingView
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Fade()
            exitTransition = Fade()
            sharedElementEnterTransition =
                TransitionInflater.from(context).inflateTransition(R.transition.trans_move)
            sharedElementExitTransition =
                TransitionInflater.from(context).inflateTransition(R.transition.trans_move)

//            WindowCompat.setDecorFitsSystemWindows(this, false)
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            ViewCompat.getWindowInsetsController(window.decorView)?.let {
                val nightModeFlags = context.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
                when (nightModeFlags) {
                    Configuration.UI_MODE_NIGHT_YES -> it.isAppearanceLightStatusBars = false
                    Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED ->
                        it.isAppearanceLightStatusBars = true
                }
            }
        }

        savedInstanceState?.let { onRestore(it) } ?: onInit()
        setContentView(FrameLayout(this).apply {
            clipChildren = false
            clipToPadding = false

            addView(FrameLayout(context).apply {
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }.also { baseContentView = it })

            addView(CMTitleBar(context).apply {
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            }.also { baseTitleBar = it })

            addView(CMLoadingView(context).apply {
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                superSetVisibility(View.GONE)
            }.also { baseLoadingView = it })
        })

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        savedInstanceStateMap.entries.forEach {
            when (it.value) {
                is Int -> outState.putInt(it.key, it.value as Int)
                is String -> outState.putString(it.key, it.value as String)
                is Parcelable -> outState.putParcelable(it.key, it.value as Parcelable)
            }
        }
    }

    private fun onInit() {
        val intent = intent ?: return
        savedIntKey().forEach {
            savedInstanceStateMap[it] = intent.getIntExtra(it, -1)
        }
        savedStringKey().forEach {
            savedInstanceStateMap[it] = intent.getStringExtra(it) ?: String()
        }
        savedParcelableKey().forEach {
            savedInstanceStateMap[it] = intent.getParcelableExtra(it) ?: Any()
        }
    }

    private fun onRestore(savedInstanceState: Bundle) {
        savedIntKey().forEach {
            savedInstanceStateMap[it] = savedInstanceState.getInt(it, -1)
        }
        savedStringKey().forEach {
            savedInstanceStateMap[it] = savedInstanceState.getString(it) ?: String()
        }
        savedParcelableKey().forEach {
            savedInstanceStateMap[it] = savedInstanceState.getParcelable(it) ?: Any()
        }
    }

    open fun savedIntKey(): List<String> = listOf()

    open fun savedStringKey(): List<String> = listOf()

    open fun savedParcelableKey(): List<String> = listOf()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getSavedState(key: String): T? {
        val value = savedInstanceStateMap[key]
        if (value != null) {
            val result = try {
                value as T
            } catch (ignored: Exception) {
                return null
            }
            return result
        }
        return null
    }
}