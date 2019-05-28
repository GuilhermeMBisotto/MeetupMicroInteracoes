package com.guilhermebisotto.meetupsample.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.guilhermebisotto.meetupsample.R
import com.guilhermebisotto.meetupsample.model.EventModel
import com.guilhermebisotto.meetupsample.utils.fadeVisibility
import kotlinx.android.synthetic.main.item_home.view.*

class HomeAdapter(
    private val items: MutableList<EventModel>,
    private val clickListener: (EventModel, ImageView) -> Unit
) : RecyclerView.Adapter<HomeAdapter.HomeHolder>() {

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        return HomeHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home, parent, false)
        )
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: HomeHolder, position: Int) {

        val item = items[position]

        holder.textViewTitle.text = item.name
        holder.textViewAddress.text = "${item.city}/${item.state}"
        holder.textViewPlace.text = item.place

        Glide.with(holder.itemView.context)
            .load(item.image)
            .placeholder(R.drawable.ic_launcher_background)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.imageViewHome)

        holder.itemView.setOnClickListener {
            clickListener(item, holder.imageViewHome)
        }

        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            viewToAnimate.fadeVisibility(true)
            lastPosition = position
        }
    }

    fun updateItems(list: List<EventModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class HomeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewHome: ImageView = itemView.imageViewHome
        val textViewTitle: TextView = itemView.textViewTitle
        val textViewAddress: TextView = itemView.textViewAddress
        val textViewPlace: TextView = itemView.textViewPlace
    }
}