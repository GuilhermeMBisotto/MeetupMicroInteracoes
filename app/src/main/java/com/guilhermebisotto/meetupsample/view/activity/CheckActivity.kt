package com.guilhermebisotto.meetupsample.view.activity

import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieResult
import com.guilhermebisotto.meetupsample.R
import kotlinx.android.synthetic.main.activity_check.*

class CheckActivity : AppCompatActivity() {

    // more specifically: loading to success: this.animation.play(0, ~386); loading to error: loading to success: this.animation.play(~400, 841);

    private lateinit var canimation: LottieResult<LottieComposition>
    private lateinit var danimation: LottieResult<LottieComposition>
    private lateinit var lanimation: LottieResult<LottieComposition>
    private lateinit var tongueAnimation: LottieResult<LottieComposition>
    private lateinit var shockAnimation: LottieResult<LottieComposition>

    private var hasInternet = true
    private val runnable = Runnable { doneAnimation.playAnimation() }
    private val h = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)

        Handler().post {
            canimation = LottieCompositionFactory.fromAssetSync(this, "check_animation.json")
            danimation = LottieCompositionFactory.fromAssetSync(this, "checked_done.json")
            lanimation = LottieCompositionFactory.fromAssetSync(this, "like.json")
            tongueAnimation = LottieCompositionFactory.fromAssetSync(this, "emoji_reaction.json")
            shockAnimation = LottieCompositionFactory.fromAssetSync(this, "crying_emoji.json")

            canimation.value?.let {
                checkAnimation.progress = 0f
                checkAnimation.setComposition(it)
                checkAnimation.repeatMode = LottieDrawable.RESTART
                checkAnimation.repeatCount = 0
            }

            danimation.value?.let {
                doneAnimation.progress = 0f
                doneAnimation.setComposition(it)
                doneAnimation.repeatMode = LottieDrawable.RESTART
                doneAnimation.repeatCount = 0
            }

            lanimation.value?.let {
                likeAnimation.progress = 0f
                likeAnimation.setComposition(it)
                likeAnimation.repeatMode = LottieDrawable.RESTART
                likeAnimation.repeatCount = 0
            }
        }

        doneAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (hasInternet && doneAnimation.tag == "error") {
                    h.removeCallbacks(runnable)
                    doneAnimation.pauseAnimation()
                    setupSuccessAnimation()
                } else if (hasInternet && doneAnimation.tag == "success") {
                    h.removeCallbacks(runnable)
                } else {
                    setupErrorAnimation()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })

        checkAnimation.setOnClickListener {
            checkAnimation.playAnimation()
        }

        doneAnimation.setOnClickListener {
            doneAnimation.playAnimation()
        }

        likeAnimation.setOnClickListener {
            likeAnimation.playAnimation()
        }

        button.setOnClickListener {
            hasInternet = false
            setupErrorAnimation()

            Handler().postDelayed({
                hasInternet = true
            }, 5_00)
        }
    }

    private fun setupErrorAnimation() {
        shockAnimation.value?.let {
            doneAnimation.progress = 0f
            doneAnimation.setComposition(it)
            doneAnimation.tag = "error"
            doneAnimation.repeatMode = LottieDrawable.RESTART
            doneAnimation.repeatCount = 2
            h.post(runnable)
        }
    }

    private fun setupSuccessAnimation() {
        tongueAnimation.value?.let {
            doneAnimation.progress = 0f
            doneAnimation.setComposition(it)
            doneAnimation.tag = "success"
            doneAnimation.repeatMode = LottieDrawable.RESTART
            doneAnimation.repeatCount = 0
            doneAnimation.playAnimation()
        }
    }
}
