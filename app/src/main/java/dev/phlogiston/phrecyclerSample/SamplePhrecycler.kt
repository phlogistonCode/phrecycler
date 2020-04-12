package dev.phlogiston.phrecyclerSample

import dev.phlogiston.phrecycler.PhrecyclerAdapter

class SamplePhrecycler(vararg clicks: (DataClass) -> Unit) : PhrecyclerAdapter<DataClass>(clicks) {

    override val setViewHolders = vhClassToLayout(
        Item1VH::class.java to R.layout.item_1,
        Item2VH::class.java to R.layout.item_2
    )

    override fun setupViewType() = { dataClass: DataClass ->
        if (dataClass.id % 3 == 0) Item1VH::class.java
        else Item2VH::class.java
    }

}