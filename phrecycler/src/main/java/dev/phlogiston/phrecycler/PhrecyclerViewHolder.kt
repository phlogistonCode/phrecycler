package dev.phlogiston.phrecycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class PhrecyclerViewHolder<LT>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    open val wholeClick = -1
    open val viewClicks = mapOf<View, Int>()

    internal var wholeClickListener: View.OnClickListener = View.OnClickListener { }
    internal val viewsClickListeners = mutableListOf<View.OnClickListener>()

    internal fun createClickListeners() {
        itemView.setOnClickListener(wholeClickListener)
        if (viewClicks.isNotEmpty() && viewsClickListeners.isNotEmpty())
            viewClicks.keys.forEachIndexed { index, view ->
                view.setOnClickListener(viewsClickListeners[index])
            }
    }

    fun viewToFuncId(vararg pairs: Pair<View, Int>)
            : Map<View, Int> = mapOf(*pairs)

    abstract fun bind(item: LT)
}