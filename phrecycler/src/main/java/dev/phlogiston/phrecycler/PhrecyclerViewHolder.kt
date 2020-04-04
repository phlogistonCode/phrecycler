package dev.phlogiston.phrecycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class PhrecyclerViewHolder<LT>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: LT)
}