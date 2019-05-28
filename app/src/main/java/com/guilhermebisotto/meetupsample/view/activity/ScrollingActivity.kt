package com.guilhermebisotto.meetupsample.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.guilhermebisotto.meetupsample.R
import com.guilhermebisotto.meetupsample.model.EventModel
import com.guilhermebisotto.meetupsample.utils.fadeVisibility
import com.guilhermebisotto.meetupsample.utils.showAnimationDialog
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ScrollingActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = CoroutineScope(Dispatchers.Main).coroutineContext

    private val event: EventModel? by lazy {
        intent.extras?.getParcelable("EVENT") as EventModel
    }

    private var isOpenOrCloseView = true
    private var needsToExpand = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        launch {
            delay(5_00)
            TransitionManager.beginDelayedTransition(scrollingContainer)
            fadeVisibility(View.VISIBLE)

            // set title da toolbar não respeita o delay criado para terminar a transição de elementos compartilhados
            delay(5_00)
            isOpenOrCloseView = false
            scrollingFab.fadeVisibility(true)
            toolbar.title = event?.name
        }

        Glide.with(this)
            .load(event?.image)
            .placeholder(R.drawable.ic_launcher_background)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(scrollingImage)

        // app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
        //     if (isOpenOrCloseView) return@OnOffsetChangedListener
        //
        //     if (Math.abs(p1) - p0.totalScrollRange == 0) {
        //         //  Collapsed
        //         frameFakeFab.fadeVisibility(false)
        //     } else {
        //         //Expanded
        //         frameFakeFab.fadeVisibility(true)
        //     }
        // })

        scrollingFab.setOnClickListener {
            val dialog = showAnimationDialog { }
            dialog.show()
        }

        scrollingButton.setOnClickListener {
            val params = scrollingView.layoutParams
            TransitionManager.beginDelayedTransition(scrollingContainer)
            if (needsToExpand) {
                scrollingButton.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimary
                    )
                )
                params.width = LinearLayout.LayoutParams.MATCH_PARENT
            } else {
                scrollingButton.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccent
                    )
                )
                params.width = 0
            }
            scrollingView.layoutParams = params
            needsToExpand = !needsToExpand
        }
    }

    private fun fadeVisibility(status: Int) {
        scrollingShadow.visibility = status
        scrollingText.visibility = status
        scrollingButton.visibility = status
        scrollingView.visibility = status
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        TransitionManager.beginDelayedTransition(scrollingContainer)
        fadeVisibility(View.GONE)
        scrollingFab.fadeVisibility(false)
        isOpenOrCloseView = true

        launch {
            delay(3_00)
            super.onBackPressed()
            overridePendingTransition(R.anim.fade_in, R.anim.animation_slide_down)
        }
    }
}
