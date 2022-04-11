package org.bbs.commonmanga

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import org.bbs.commonmanga.base.CMBaseActivity
import org.bbs.commonmanga.ui.CMMangaDetailActivity
import org.bbs.commonmanga.ui.CMSearchActivity
import org.bbs.commonmanga.utils.CMStoreUtils.Companion.KEY_SITE_MAO_FLY
import org.bbs.commonmanga.viewmodel.CMMangaDetail.Companion.KEY_CM_MANGA_ID
import org.bbs.commonmanga.viewmodel.CMMangaDetailVM
import org.bbs.commonmanga.viewmodel.CMSearchResultVM
import org.bbs.commonmanga.widgets.CMSearchView

class CMMainActivity : CMBaseActivity() {
    private lateinit var detailVM: CMMangaDetailVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailVM = ViewModelProvider(this).get(CMMangaDetailVM::class.java)
        baseTitleBar.setVisibilityWithStubShow(false)
        baseContentView.addView(createContentView())
    }

    private fun createContentView() = LinearLayout(this).apply {
        setBackgroundResource(getResByAttrs(R.attr.colorOnPrimary))

        addView(
            CMSearchView(context).apply {
                isFocusable = false
                isFocusableInTouchMode = false
                inputType = InputType.TYPE_NULL

                setOnClickListener {
                    if (true) {
                        startActivity(
                            Intent(
                                this@CMMainActivity,
                                CMMangaDetailActivity::class.java
                            ).apply {
                                putExtra(KEY_CM_MANGA_ID, "9618")
                                putExtra(
                                    CMMangaDetailActivity.KEY_FROM_SITE,
                                    KEY_SITE_MAO_FLY
                                )
                            })
                    }
                    if (false) {
                        val intent = Intent(context, CMSearchActivity::class.java)
                        val options = ActivityOptions.makeSceneTransitionAnimation(
                            context as Activity,
                            this,
                            CMSearchView.TRANS_NAME
                        )
                        context.startActivity(intent, options.toBundle())
                    }
                    ViewModelProvider(this@CMMainActivity)
                        .get(CMSearchResultVM::class.java)
                        .fetchSearchResult("厨", 4)
                }
            },
            LinearLayout.LayoutParams(MATCH_PARENT, dip2px(48F)).apply {
                gravity = Gravity.CENTER
                marginStart = dip2px(36F)
                marginEnd = dip2px(36F)
            }
        )
    }
}