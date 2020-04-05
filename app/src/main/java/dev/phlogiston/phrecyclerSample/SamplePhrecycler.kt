package dev.phlogiston.phrecyclerSample

import dev.phlogiston.phrecycler.PhrecyclerAdapter

class SamplePhrecycler(private val click: (DataClass) -> Unit,
                       click1: (DataClass) -> Unit,
                       click2: (DataClass) -> Unit,
                       click3: (DataClass) -> Unit) : PhrecyclerAdapter<DataClass>(click1, click2, click3) {

    override val setViewHolders = mapOf(
        Item1VH::class.java to R.layout.item_1,
        Item2VH::class.java to R.layout.item_2
    )

    override fun determineVTFunc() = { dataClass: DataClass ->
        if (dataClass.id % 3 == 0) Item1VH::class.java
        else Item2VH::class.java
    }

    override fun itemClick() = click
}