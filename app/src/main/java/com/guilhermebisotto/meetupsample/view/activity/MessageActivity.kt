package com.guilhermebisotto.meetupsample.view.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.guilhermebisotto.meetupsample.R
import com.guilhermebisotto.meetupsample.utils.setupReveal
import com.guilhermebisotto.meetupsample.utils.slideDown
import com.guilhermebisotto.meetupsample.utils.slideUp
import com.guilhermebisotto.meetupsample.view.adapter.MessageAdapter
import com.guilhermebisotto.meetupsample.viewmodel.MessageViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MessageActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = CoroutineScope(Dispatchers.Main).coroutineContext

    companion object {
        const val EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X"
        const val EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y"
    }

    private lateinit var messageViewModel: MessageViewModel
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        setSupportActionBar(toolbar)

        messageViewModel = ViewModelProviders.of(this).get(MessageViewModel::class.java).apply {
            getItems()
        }

        intent?.let {
            if (it.hasExtra(EXTRA_CIRCULAR_REVEAL_X)
                && it.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)
            ) {
                bgMessage.setupReveal(
                    intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0),
                    intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0)
                )
            } else {
                simpleFade()
            }
        } ?: run {
            simpleFade()
        }

        launch {
            setupAnimations()
            delay(2_00)
            TransitionManager.beginDelayedTransition(headerMessage)
            headerMessage.visibility = View.VISIBLE
            recyclerViewMessage.visibility = View.VISIBLE
        }

        bgMessage.setOnClickListener {
            this.onBackPressed()
        }

        setupObservables()
        setupRecyclerView()
    }

    private fun simpleFade() {
        TransitionManager.beginDelayedTransition(constraintMessage)
        bgMessage.visibility = View.VISIBLE
    }

    private fun setupObservables() {
        messageViewModel.messageItems.observe(this, Observer {
            adapter.updateItems(it)
        })
    }

    private fun setupRecyclerView() {
        adapter = MessageAdapter(mutableListOf())

        recyclerViewMessage.layoutManager = LinearLayoutManager(this)
        recyclerViewMessage.adapter = adapter

        recyclerViewMessage.setOnScrollChangeListener { _, _, _, _, _ ->
            headerMessage.isSelected = recyclerViewMessage.canScrollVertically(-1)
        }
    }

    override fun onBackPressed() {
        TransitionManager.beginDelayedTransition(constraintMessage)
        headerMessage.visibility = View.GONE
        recyclerViewMessage.visibility = View.GONE
        outScreenAnimation()
        bgMessage.visibility = View.GONE

        launch {
            delay(3_00)
            super.onBackPressed()
            overridePendingTransition(0, 0)
        }
    }

    private fun setupAnimations() {
        launch {
            delay(2_00)
            inScreenAnimation()
        }
    }

    private fun inScreenAnimation() {
        llContainer.slideUp(250)
    }

    private fun outScreenAnimation() {
        llContainer.slideDown(250)
    }
}
