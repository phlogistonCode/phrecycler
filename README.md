# phrecycler

This is a library that simplifies working with Recycler View.

Android API：minimum API 14 support for Android **4.0**

**1. Gradle**

    implementation 'com.wei.android.lib:fingerprintidentify:1.2.6'

**2. PhrecyclerAdapter**

    class SamplePhrecycler(
    private val click: (DataClass) -> Unit,
    click1: (DataClass) -> Unit,
    click2: (DataClass) -> Unit,
    click3: (DataClass) -> Unit
    ) : PhrecyclerAdapter<DataClass>(click1, click2, click3) {

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

**3. PhrecyclerViewHolder**

    class Item1VH(itemView: View) : PhrecyclerViewHolder<DataClass>(itemView) {

    override fun bind(item: DataClass) = with(itemView) {
        text.text = "ITEM1: ${item.id}"
    }

    override fun viewClick() = viewToFunc(mapOf(itemView.clickCheck1 to 0))
    }
 ________________________________________________________________________________
    
    class Item2VH(itemView: View) : PhrecyclerViewHolder<DataClass>(itemView) {

    override fun bind(item: DataClass) = with(itemView) {
        text.text = "ITEM2: ${item.id}"
    }

    override fun viewClick() = viewToFunc(mapOf(
        itemView.clickCheck2 to 1,
        itemView.clickCheck3 to 2
    ))
    }

## License ##

Licensed under the Apache License, see the [LICENSE](https://github.com/phlogistonCode/phrecycler/blob/master/LICENSE) for copying permission.
