package com.guilhermebisotto.meetupsample.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.Display
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.guilhermebisotto.meetupsample.R
import com.guilhermebisotto.meetupsample.model.EventModel
import com.guilhermebisotto.meetupsample.view.adapter.HomeAdapter
import com.guilhermebisotto.meetupsample.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = CoroutineScope(Dispatchers.Main).coroutineContext

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: HomeAdapter
    private var needsToUpdate = true
    private var itemsCount = 0

    private companion object Params {
        const val STIFFNESS = SpringForce.STIFFNESS_LOW
        const val DAMPING_RATIO = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
    }

    private lateinit var xAnimation: SpringAnimation
    private lateinit var yAnimation: SpringAnimation
    var fabMoved = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        setupObservables()
        setupRecyclerView()
        setupFabMoveAnimation()
        initLoader()
    }

    private fun setupObservables() {
        homeViewModel.events.observe(this, Observer {
            swipeRefreshLayoutHome.isRefreshing = false
            adapter.updateItems(it)

            launch {
                if (needsToUpdate) {
                    if (itemsCount > 20) {
                        needsToUpdate = false

                        delay(2_00)
                        TransitionManager.beginDelayedTransition(mainContainer)
                        mainLoader.visibility = View.GONE
                    } else {
                        delay(2_00)
                        itemsCount += 1
                        homeViewModel.getItems(itemsCount)
                    }
                }
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = HomeAdapter(mutableListOf()) { event, img ->
            openScrollingScreen(event, img)
        }

        recyclerViewHome.layoutManager = LinearLayoutManager(this)
        recyclerViewHome.adapter = adapter

        recyclerViewHome.setOnScrollChangeListener { _, _, _, _, _ ->
            header.isSelected = recyclerViewHome.canScrollVertically(-1)
        }

        swipeRefreshLayoutHome.setOnRefreshListener {
            launch {
                adapter.updateItems(listOf())
                delay(3_00)
                TransitionManager.beginDelayedTransition(mainContainer)
                mainLoader.visibility = View.VISIBLE

                itemsCount = 0
                needsToUpdate = true
                homeViewModel.getItems(itemsCount)
            }
        }
    }

    private fun initLoader() {
        loaderScrollview.setOnScrollChangeListener { _, _, _, _, _ ->
            header.isSelected = loaderScrollview.canScrollVertically(-1)
        }

        launch {
            TransitionManager.beginDelayedTransition(mainContainer)
            mainLoader.visibility = View.VISIBLE
            mainLoader.playAnimation()

            delay(3_000)

            itemsCount += 1
            homeViewModel.getItems(itemsCount)
        }
    }

    private fun setupFabMoveAnimation() {
        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y
        var dX = 0f
        var dY = 0f
        val gestureDetector = GestureDetector(this, GestureTap())

        fab.setOnTouchListener { view, event ->
            gestureDetector.onTouchEvent(event)

            var x = (event.rawX + dX)
            var y = (event.rawY + dY)
            val xDelta = if (x > width / 2) -64 else 64
            val yDelta = if (y > height / 2) -128 else 128

            if (x > width) x = width.toFloat() + dX
            if (y > height) y = height.toFloat() + dY

            xAnimation = createSpringAnimation(
                fab, SpringAnimation.X, x + xDelta, STIFFNESS, DAMPING_RATIO
            )
            yAnimation = createSpringAnimation(
                fab, SpringAnimation.Y, y + yDelta, STIFFNESS, DAMPING_RATIO
            )

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    // capture the difference between view's top left corner and touch point
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY

                    // cancel animations so we can grab the view during previous animation
                    xAnimation.cancel()
                    yAnimation.cancel()
                }
                MotionEvent.ACTION_MOVE -> {
                    //  a different approach would be to change the view's LayoutParams.
                    fab.animate()
                        .x(x)
                        .y(y)
                        .setDuration(0)
                        .start()
                }
                MotionEvent.ACTION_UP -> {
                    if (fabMoved) {
                        xAnimation.start()
                        yAnimation.start()
                    }
                }
            }
            true
        }
    }

    inner class GestureTap : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            openMessageScreen()
            fabMoved = false
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            fabMoved = true
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }

    private fun createSpringAnimation(
        view: View,
        property: DynamicAnimation.ViewProperty,
        finalPosition: Float,
        stiffness: Float,
        dampingRatio: Float
    ): SpringAnimation {
        val animation = SpringAnimation(view, property)
        val spring = SpringForce(finalPosition)
        spring.stiffness = stiffness
        spring.dampingRatio = dampingRatio
        animation.spring = spring
        return animation
    }

    private fun openMessageScreen() {
        val intent = Intent(this, MessageActivity::class.java)
        val revealX = mainCoordinator.width / 2
        val revealY = mainCoordinator.height / 2
        intent.apply {
            putExtra(MessageActivity.EXTRA_CIRCULAR_REVEAL_X, revealX)
            putExtra(MessageActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY)
        }

        startActivity(intent)
    }

    private fun openScrollingScreen(event: EventModel, img: ImageView) {
        val intent = Intent(this, ScrollingActivity::class.java)

        val args = Bundle()
        args.putParcelable("EVENT", event)
        intent.putExtras(args)

        val comp: Pair<View, String> = Pair(img, "sharedImage")
        val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            comp
        )
        startActivity(intent, options.toBundle())
        overridePendingTransition(R.anim.animation_slide_up, R.anim.fade_out)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
