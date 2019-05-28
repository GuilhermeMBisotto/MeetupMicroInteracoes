package com.guilhermebisotto.meetupsample.utils

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.TranslateAnimation
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun View.doBounceAnimation() {
    val animator = ObjectAnimator.ofFloat(this, "translationY", 25f, 0f, 25f)
    animator.interpolator = EasingInterpolator(Ease.ELASTIC_IN_OUT)
    animator.duration = 750
    animator.start()
}

fun View.slideUp(duration: Int = 500) {
    visibility = View.VISIBLE
    val animate = TranslateAnimation(0f, 0f, this.height.toFloat(), 0f)
    animate.duration = duration.toLong()
    animate.fillAfter = true
    this.startAnimation(animate)
}

fun View.slideDown(duration: Int = 500) {
    visibility = View.VISIBLE
    val animate = TranslateAnimation(0f, 0f, 0f, this.height.toFloat())
    animate.duration = duration.toLong()
    animate.fillAfter = true
    this.startAnimation(animate)
}

fun View.setupReveal(revealX: Int = 0, revealY: Int = 0) {
    val view = this
    if (revealX != 0 && revealY != 0) {
        view.visibility = View.INVISIBLE

        val viewTreeObserver = view.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    revealActivity(revealX, revealY)
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    } else {
        view.visibility = View.VISIBLE
    }
}

fun View.revealActivity(x: Int, y: Int) {
    val finalRadius =
        (Math.max(this.width, this.height) * 1.1).toFloat()

    val circularReveal =
        ViewAnimationUtils.createCircularReveal(this, x, y, 0f, finalRadius)
    circularReveal.duration = 400
    circularReveal.interpolator = AccelerateInterpolator()

    this.visibility = View.VISIBLE
    circularReveal.start()
}

fun View.fadeVisibility(show: Boolean) {
    val animationTime = this.context.resources.getInteger(android.R.integer.config_longAnimTime)
    this.apply {
        if (show) {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).duration = animationTime.toLong()
        } else {
            alpha = 1f
            animate().alpha(0f).duration = animationTime.toLong()
            visibility = View.GONE
        }
    }
}

class ScrollAwareFABBehavior(context: Context?, attrs: AttributeSet?) :
    FloatingActionButton.Behavior(context, attrs) {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        // Ensure we react to vertical scrolling
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
            || super.onStartNestedScroll(
            coordinatorLayout,
            child,
            directTargetChild,
            target,
            axes,
            type
        )
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type
        )

        if (dyConsumed > 0 && child.visibility == View.VISIBLE) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            child.hide()
        } else if (dyConsumed < 0 && child.visibility != View.VISIBLE) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            child.show()
        }
    }
}