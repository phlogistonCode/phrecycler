package dev.phlogiston.phrecyclerSample

import android.view.View
import dev.phlogiston.phrecycler.PhrecyclerViewHolder
import kotlinx.android.synthetic.main.item_2.view.*

class Item2VH(itemView: View) : PhrecyclerViewHolder<DataClass>(itemView) {

    override fun bind(item: DataClass) = with(itemView) {
        text.text = "ITEM2: ${item.id}"
    }

    override fun viewClick() = viewToFunc(mapOf(
        itemView.clickCheck2 to 1,
        itemView.clickCheck3 to 2
    ))
}