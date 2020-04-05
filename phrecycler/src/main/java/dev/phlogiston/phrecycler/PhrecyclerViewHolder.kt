package dev.phlogiston.phrecycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class PhrecyclerViewHolder<LT>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: LT)

    open fun viewClicks() : Pair<Map<View?, Int>, Class<out RecyclerView.ViewHolder>> = Pair(mapOf(), this::class.java)

    fun RecyclerView.ViewHolder.viewToFunc(viewToFuncId: Map<View?, Int>)
            : Pair<Map<View?, Int>, Class<out RecyclerView.ViewHolder>> =
        Pair(viewToFuncId, this::class.java)
}