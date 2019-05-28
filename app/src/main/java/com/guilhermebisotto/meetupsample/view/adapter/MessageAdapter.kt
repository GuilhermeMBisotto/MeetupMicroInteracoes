package com.guilhermebisotto.meetupsample.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.guilhermebisotto.meetupsample.R
import kotlinx.android.synthetic.main.item_message_received.view.*
import kotlinx.android.synthetic.main.item_message_sent.view.*

class MessageAdapter(
    private val items: MutableList<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_SENT = 0
    private val TYPE_RECEIVED = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SENT -> {
                MessageSentHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message_sent, parent, false)
                )
            }
            TYPE_RECEIVED -> {
                MessageReceivedHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message_received, parent, false)
                )
            }
            else -> {
                throw RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isPositionSent(position)) {
            TYPE_SENT
        } else {
            TYPE_RECEIVED
        }
    }

    private fun isPositionSent(position: Int): Boolean {
        return position % 2 == 0
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = items[position]

        if (holder is MessageReceivedHolder) {
            holder.textViewReceivedMessage.text = item
        } else if (holder is MessageSentHolder) {
            holder.textViewSentMessage.text = item
        }
    }

    fun updateItems(list: List<String>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class MessageSentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewSentMessage: TextView = itemView.textViewSentMessage
    }

    class MessageReceivedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewReceivedMessage: TextView = itemView.textViewReceivedMessage
    }
}