package dev.phlogiston.phrecyclerSample

import dev.phlogiston.phrecycler.PhrecyclerAdapter

class SamplePhrecycler : PhrecyclerAdapter<DataClass>() {

    override val setViewHolders = mapOf(
        Item1VH::class.java to R.layout.item_1,
        Item2VH::class.java to R.layout.item_2
    )

    override fun determineVTFunc() = { dataClass: DataClass ->
        if (dataClass.id % 3 == 0) Item1VH::class.java
        else Item2VH::class.java
    }
}