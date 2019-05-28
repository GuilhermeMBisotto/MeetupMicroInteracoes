package com.guilhermebisotto.meetupsample.view.customview

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.card.MaterialCardView

class MaterialCardTranslation @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private var isElevated: Boolean = true
    private var ANIMATION_DURATION: Long = 250

    fun isElevated(): Boolean {
        return isElevated
    }

    fun setDuration(duration: Long) {
        ANIMATION_DURATION = duration
    }

    fun elevateCard(elevate: Boolean, endAction: () -> Unit) {
        dispatchElevateAnim(elevate, endAction)
    }

    fun simulateClickEvent(elevate: Boolean, endAction: () -> Unit) {
        dispatchElevateCompleteAnim(endAction)
    }

    private fun dispatchElevateAnim(elevate: Boolean, endAction: () -> Unit) {
        if (elevate) {
            upAnim()
        } else {
            downAnim()
        }

        isElevated = elevate

        Handler().postDelayed({
            endAction()
        }, ANIMATION_DURATION)
    }

    private fun dispatchElevateCompleteAnim(endAction: () -> Unit) {
        val posZ = z

        animate().translationZ(posZ * 2).setDuration(ANIMATION_DURATION)
            .setInterpolator(FastOutSlowInInterpolator())
            .withEndAction { downAnim() }

        Handler().postDelayed({
            endAction()
        }, ANIMATION_DURATION * 2)
    }

    private fun upAnim() {
        animate().translationZ(z * 2).setDuration(ANIMATION_DURATION)
            .setInterpolator(FastOutSlowInInterpolator())
            .start()
    }

    private fun downAnim() {
        animate().translationZ(0f).setDuration(ANIMATION_DURATION)
            .setInterpolator(FastOutSlowInInterpolator())
            .start()
    }
}