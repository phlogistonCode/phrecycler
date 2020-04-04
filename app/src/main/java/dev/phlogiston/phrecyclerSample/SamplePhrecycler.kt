package dev.phlogiston.phrecyclerSample

import androidx.recyclerview.widget.RecyclerView
import dev.phlogiston.phrecycler.PhrecyclerAdapter

class SamplePhrecycler : PhrecyclerAdapter<DataClass>() {

    override val setViewHolders = mapOf(
        Item1VH::class.java to R.layout.item_1,
        Item2VH::class.java to R.layout.item_2
    )

    override val determineVTFunc: (DataClass) -> Class<out RecyclerView.ViewHolder> = {
        if (it.id % 3 == 0) Item1VH::class.java
        else Item2VH::class.java
    }

}