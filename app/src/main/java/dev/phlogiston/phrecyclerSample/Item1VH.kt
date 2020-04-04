package dev.phlogiston.phrecyclerSample

import android.view.View
import dev.phlogiston.phrecycler.PhrecyclerViewHolder
import kotlinx.android.synthetic.main.item_1.view.*

class Item1VH(itemView: View) : PhrecyclerViewHolder<DataClass>(itemView) {

    override fun bind(item: DataClass) = with(itemView) {
        text.text = "ITEM1: ${item.id}"
    }


}