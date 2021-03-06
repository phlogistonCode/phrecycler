package dev.phlogiston.phrecyclerSample

import android.view.View
import dev.phlogiston.phrecycler.PhrecyclerViewHolder
import kotlinx.android.synthetic.main.item_2.view.*

class Item2VH(itemView: View) : PhrecyclerViewHolder<DataClass>(itemView) {

    override fun bind(item: DataClass) = with(itemView) {
        text.text = "ITEM2: ${item.id}"
    }

    override val wholeClick = 0

    override val viewClicks = viewToFuncId(
        itemView.clickCheck2 to 2,
        itemView.clickCheck3 to 3
    )
}