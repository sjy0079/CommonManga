package org.bbs.commonmanga.ui

import android.animation.Animator
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.ViewModelProvider
import org.bbs.commonmanga.*
import org.bbs.commonmanga.base.CMBaseActivity
import org.bbs.commonmanga.databinding.CmMangaDetailLayoutBinding
import org.bbs.commonmanga.viewmodel.CMMangaDetail
import org.bbs.commonmanga.viewmodel.CMMangaDetail.Companion.KEY_CM_MANGA_ID
import org.bbs.commonmanga.viewmodel.CMMangaDetailVM
import org.bbs.commonmanga.viewmodel.CMMangaFavVM
import org.bbs.commonmanga.viewmodel.CMUIState
import pl.droidsonroids.gif.GifDrawable

class CMMangaDetailActivity : CMBaseActivity() {
    companion object {
        const val KEY_FROM_SITE = "cm_manga_detail_from_site"
    }

    private lateinit var binding: CmMangaDetailLayoutBinding
    private lateinit var detailVM: CMMangaDetailVM
    private lateinit var mangaFavListVM: CMMangaFavVM
    private var favButtonAnim: Animator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailVM = ViewModelProvider(this).get(CMMangaDetailVM::class.java)
        mangaFavListVM = ViewModelProvider(CMApplication.instance).get(CMMangaFavVM::class.java)
        initView()
        registerObserver()
        fetchData()
    }

    override fun savedStringKey() = listOf(KEY_CM_MANGA_ID, KEY_FROM_SITE)

    private fun initView() {
        binding = CmMangaDetailLayoutBinding.inflate(layoutInflater, baseContentView, true)
        binding.apply {
            root.setPaddingRelative(0, 0, 0, getNavigationBarHeight())
            scrollContainer.apply {
                visibility = View.GONE
                setOnScrollChangeListener { _, _, _, _, _ ->
                    setTitleAlphaByScrollerY()
                }
            }
            infoBoard.background.colorFilter = PorterDuffColorFilter(
                getColorByAttrs(R.attr.cmMaskColorRev),
                PorterDuff.Mode.SRC_ATOP
            )
            infoContainer.setPaddingRelative(0, getStatusBarHeight() + dip2px(56F), 0, 0)
            favMangaBtn.apply {
                background =
                    createSolidRipple(getColorByAttrs(R.attr.colorPrimaryVariant))
                setTextColor(getColor(R.color.white))
            }
            continueReadingBtn.apply {
                background =
                    createSolidRipple(getColor(R.color.white), getColor(R.color.cm_mask_black))
                setTextColor(getColorByAttrs(R.attr.colorPrimaryVariant))
                setOnClickListener { }
            }
        }
        baseLoadingView.apply {
            visibility = View.VISIBLE
            setRefreshListener {
                fetchData()
            }
        }
    }

    private fun registerObserver() {
        val site = getSavedState(KEY_FROM_SITE) ?: String()
        val id = getSavedState(KEY_CM_MANGA_ID) ?: String()

        detailVM.getUIState().observe(this) {
            when (it) {
                is CMUIState.LOADING -> {
                    binding.apply {
                        scrollContainer.visibility = View.GONE
                        baseLoadingView.apply {
                            visibility = View.VISIBLE
                            showLoading()
                        }
                    }
                }
                is CMUIState.ERROR -> {
                    binding.apply {
                        scrollContainer.visibility = View.GONE
                        baseLoadingView.apply {
                            visibility = View.VISIBLE
                            showFailPage(it.info)
                        }
                    }
                }
                is CMUIState.COMPLETE -> {
                    setTitleAlphaByScrollerY()
                    binding.apply {
                        createAnimator(0F, 1F, 300L, { alpha ->
                            scrollContainer.alpha = alpha
                        }, {
                            scrollContainer.apply {
                                alpha = 0F
                                visibility = View.VISIBLE
                            }
                        }).start()
                        baseLoadingView.visibility = View.GONE
                    }
                }
            }
        }

        detailVM.getDetail().observe(this) { mangaDetail ->
            baseTitleBar.setTitle(mangaDetail.title)
            binding.apply {
                if (mangaDetail.originTitle.isBlank()) {
                    originNameTv.visibility = View.GONE
                } else {
                    originNameTv.apply {
                        visibility = View.VISIBLE
                        text = mangaDetail.originTitle
                        setIcon(R.drawable.cm_book_icon)
                    }
                }
                setupBoardDecor()
                coverIv.loadUrl(mangaDetail.coverUrl)
                authorTv.apply {
                    text = mangaDetail.author
                    setIcon(R.drawable.cm_paint_icon)
                }
                updateTimeTv.apply {
                    text = mangaDetail.updateTime
                    setIcon(R.drawable.cm_update_icon)
                }
                statusTv.apply {
                    text = mangaDetail.status
                    setIcon(R.drawable.cm_status_icon)
                }
                setupSectionContainer(mangaDetail)
            }
        }

        mangaFavListVM.getFavList(site).observe(this) {
            binding.favMangaBtn.apply {
                if (it.contains(id)) {
                    // do dislike
                    background =
                        createSolidRipple(getColorByAttrs(R.attr.colorPrimary))
                    setText(R.string.cm_manga_detail_dislike)
                    setOnClickListener {
                        if (favButtonAnim != null) {
                            return@setOnClickListener
                        }
                        mangaFavListVM.dislike(site, id)
                        playFavAnim(R.drawable.capoo_dislike)
                    }
                } else {
                    // do fav
                    background =
                        createSolidRipple(getColorByAttrs(R.attr.colorPrimaryVariant))
                    setText(R.string.cm_manga_detail_fav)
                    setOnClickListener {
                        if (favButtonAnim != null) {
                            return@setOnClickListener
                        }
                        mangaFavListVM.fav(site, id)
                        playFavAnim(R.drawable.capoo_like)
                    }
                }
            }
        }
    }

    private fun fetchData() {
        detailVM.fetchDetail(getSavedState(KEY_CM_MANGA_ID) ?: String())
    }

    private fun setupBoardDecor() {
        binding.apply {
            if (infoBoardFront1.width == 0 || infoBoardFront2.width == 0) {
                infoBoardFront1.post {
                    setupBoardDecor()
                }
                return@apply
            }
            infoBoardFront1.post {
                infoBoardFront1.run {
                    layoutParams = layoutParams.also {
                        it.height = width * 131 / 510
                    }
                }
            }
            infoBoardFront2.post {
                infoBoardFront2.run {
                    layoutParams = layoutParams.also {
                        it.height = width * 91 / 510
                    }
                }
            }
        }
    }

    private fun setTitleAlphaByScrollerY() {
        val scroller = binding.scrollContainer
        val scrollY = scroller.scrollY
        val titleDelta = dip2px(20F)
        when {
            scrollY > baseTitleBar.height + titleDelta -> {
                baseTitleBar.setBgAlpha(1F)
            }
            scrollY < titleDelta -> {
                baseTitleBar.setBgAlpha(0F)
            }
            else -> {
                baseTitleBar.setBgAlpha((scrollY.toFloat() - titleDelta) / baseTitleBar.height)
            }
        }
    }

    private fun setupSectionContainer(mangaDetail: CMMangaDetail) {
        binding.sectionContainer.also { it.removeAllViews() }.apply {
            mangaDetail.sectionList?.forEach Section@{
                if (!it.isValid()) {
                    return@Section
                }
                addView(createSectionView(it.sectionName))
                // 100dp add one column
                val singleLineCount = getScreenWidthDp() / 100 + 1
                val epWidth =
                    (getScreenWidthDp() - 32F - 8F * (singleLineCount - 1)) / singleLineCount
                var tempCount = singleLineCount
                var columnContainer: LinearLayout? = null
                it.epList.forEach { epPreview ->
                    if (!epPreview.isValid()) {
                        return@forEach
                    }
                    if (columnContainer == null) {
                        columnContainer = LinearLayout(this@CMMangaDetailActivity).apply {
                            layoutParams =
                                LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                                    .also { lp ->
                                        lp.bottomMargin = dip2px(8F)
                                    }
                        }
                    }
                    if (tempCount != singleLineCount) {
                        columnContainer?.addView(View(context).apply {
                            layoutParams =
                                LinearLayout.LayoutParams(dip2px(8F), WRAP_CONTENT)
                        })
                    }
                    columnContainer?.addView(
                        createEpView(
                            dip2px(epWidth),
                            epPreview.epName,
                            epPreview.epUrl
                        )
                    )
                    tempCount--
                    if (tempCount == 0) {
                        tempCount = singleLineCount
                        addView(columnContainer)
                        columnContainer = null
                    }
                }
                if (columnContainer != null) {
                    addView(columnContainer)
                }
            }
        }
    }

    private fun playFavAnim(@DrawableRes resId: Int) {
        favButtonAnim = createAnimator(0F, 1F, 200, {
            binding.favMangaAnim.alpha = it
        }, onEnd = {
            binding.favMangaAnim.alpha = 1F
            (binding.favMangaAnim.drawable as GifDrawable).apply {
                addAnimationListener {
                    createAnimator(1F, 0F, 200, {
                        binding.favMangaAnim.alpha = it
                    }, onEnd = {
                        binding.favMangaAnim.alpha = 0F
                        favButtonAnim = null
                    }).start()
                }
            }
        })
        binding.favMangaAnim.apply {
            setImageResource(resId)
            (drawable as GifDrawable).loopCount = 1
        }
        favButtonAnim?.start()
    }

    private fun createSectionView(title: String): View =
        LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                topMargin = dip2px(6F)
                bottomMargin = dip2px(18F)
            }
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.START.or(Gravity.CENTER_VERTICAL)
            addView(View(context).apply {
                layoutParams = LinearLayout.LayoutParams(dip2px(4F), dip2px(4F)).apply {
                    marginEnd = dip2px(8F)
                }
                background = createSolidRipple(getColorByAttrs(R.attr.colorPrimary))
            })
            addView(AppCompatTextView(context).apply {
                text = title
                textSize = 18F
                maxLines = 1
                paint.isFakeBoldText = true
                setTextColor(getColorByAttrs(R.attr.cmTextSubColor))
            })
        }

    private fun createEpView(width: Int, title: String, url: String): View =
        AppCompatTextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(width, dip2px(36F))
            textSize = 14F
            maxLines = 1
            gravity = Gravity.CENTER
            background =
                createStrokeRipple(
                    dip2px(1F),
                    getColorByAttrs(R.attr.colorPrimary),
                    getColorByAttrs(R.attr.colorSecondary)
                )
            setPaddingRelative(dip2px(8F), 0, dip2px(8F), 0)
            setTextColor(getColorByAttrs(R.attr.colorPrimary))
            setOnClickListener {
                Toast.makeText(context, url, Toast.LENGTH_SHORT).show()
            }
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                this,
                8,
                14,
                2,
                TypedValue.COMPLEX_UNIT_SP
            )
            post {
                text = title
            }
        }

    /**
     * create button bg ripple drawable
     */
    private fun createSolidRipple(
        @ColorInt contentColor: Int,
        @ColorInt colorMask: Int = getColorByAttrs(R.attr.cmMaskColor)
    ): Drawable {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999F
            setColor(contentColor)
        }
        val stateList = ColorStateList.valueOf(colorMask)
        return RippleDrawable(stateList, StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_enabled), drawable)
        }, null)
    }

    /**
     * create button bg ripple drawable
     */
    private fun createStrokeRipple(
        width: Int,
        @ColorInt contentColor: Int,
        @ColorInt colorMask: Int = getColorByAttrs(R.attr.cmMaskColor)
    ): Drawable {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999F
            setStroke(width, contentColor)
        }
        val stateList = ColorStateList.valueOf(colorMask)
        val mask = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999F
            setColor(getColor(R.color.white))
        }
        return RippleDrawable(stateList, StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_enabled), drawable)
        }, mask)
    }

    private fun TextView.setIcon(id: Int) {
        compoundDrawablePadding = dip2px(6F)
        setCompoundDrawablesRelative(
            getDrawableWithColor(id, getColorByAttrs(R.attr.cmTextSubColor))?.apply {
                setBounds(0, dip2px(1F), dip2px(16F), dip2px(17F))
            },
            null, null, null
        )
    }
}