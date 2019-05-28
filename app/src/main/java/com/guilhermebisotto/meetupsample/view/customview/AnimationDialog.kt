package com.guilhermebisotto.meetupsample.view.customview

import android.animation.Animator
import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.transition.TransitionManager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieResult
import com.guilhermebisotto.meetupsample.R

class AnimationDialog(private var context: Context) : BaseDialogHelper() {

    private val animation: LottieResult<LottieComposition> by lazy {
        LottieCompositionFactory.fromAssetSync(context, "loader_success_failed.json")
    }

    private val runnable = Runnable { dialogAnimation.playAnimation() }
    private val h = Handler()
    private var needsConfiguration = true

    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.dialog_animation, null)
    }

    override val builder: AlertDialog.Builder =
        AlertDialog.Builder(context, R.style.RoundAlertDialog).setView(dialogView)

    private val dialogCheckBox: CheckBox by lazy {
        dialogView.findViewById<CheckBox>(R.id.dialogCheckBox)
    }

    private val dialogAnimation: LottieAnimationView by lazy {
        dialogView.findViewById<LottieAnimationView>(R.id.dialogAnimation)
    }

    private val dialogContainer: LinearLayout by lazy {
        dialogView.findViewById<LinearLayout>(R.id.dialogContainer)
    }

    private val dialogText: TextView by lazy {
        dialogView.findViewById<TextView>(R.id.dialogText)
    }

    init {
        dialogAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {

                if (dialogCheckBox.isChecked && dialogAnimation.tag == "error") {
                    h.removeCallbacks(runnable)
                    dialogAnimation.pauseAnimation()
                    onSuccessAnimation()
                } else if (dialogCheckBox.isChecked && dialogAnimation.tag == "success") {
                    h.removeCallbacks(runnable)
                    dialog?.dismiss()
                } else {
                    configureAnimation()
                }
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {}
        })

        dialogCheckBox.setOnCheckedChangeListener { _, isChecked ->

            TransitionManager.beginDelayedTransition(dialogContainer)
            if (isChecked) dialogText.visibility = View.GONE
            else dialogText.visibility = View.VISIBLE
        }

        TransitionManager.beginDelayedTransition(dialogContainer)
        dialogAnimation.visibility = View.VISIBLE
        configureAnimation()
    }

    fun configureAnimation() {
        animation.value?.let {
            dialogAnimation.progress = 0f
            dialogAnimation.setComposition(it)
            dialogAnimation.tag = "error"
            dialogAnimation.repeatMode = LottieDrawable.RESTART
            dialogAnimation.repeatCount = 0
            dialogAnimation.setMinAndMaxFrame(427, 841)
            h.post(runnable)
        }
    }

    private fun onSuccessAnimation() {
        animation.value?.let {
            dialogAnimation.progress = 0f
            dialogAnimation.setComposition(it)
            dialogAnimation.tag = "success"
            dialogAnimation.repeatMode = LottieDrawable.RESTART
            dialogAnimation.repeatCount = 0
            dialogAnimation.setMinAndMaxFrame(0, 386)
            dialogAnimation.playAnimation()
            needsConfiguration = true
        }
    }
}