# phrecycler

This is a library that simplifies working with Recycler View.

Android API：minimum API 14 support for Android **4.0**

**1. Gradle**

    implementation 'dev.phlogiston:phrecycler:1.0'

**2. PhrecyclerAdapter**

Override setViewHolders:
- If You have one ViewHolder, then transfer only the map with holder class to its layout id.
- If You have several ViewHolder, then transfer several classes of the holder to its layout id and override `setUpViewType()`, write logic for return ViewHolder class

To handle clicks throughout VievHolder, override itemClick()

Example:

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

    override fun setUpViewType() = { dataClass: DataClass ->
        if (dataClass.id % 3 == 0) Item1VH::class.java
        else Item2VH::class.java
    }

    override fun itemClick() = click
    }

**3. PhrecyclerViewHolder**

Override bind() to bind code to a view.

To handle custom clicks, override the viewClicks() method and pass the map with the view to the number of the clicker function passed in the adapter implementation. (For example: click1: (DataClass) -> Unit - this is 0; click2: (DataClass) -> Unit - this is 1; click3: (DataClass) -> Unit - this is 2)

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

    override fun viewClicks() = viewToFunc(mapOf(
        itemView.clickCheck2 to 1,
        itemView.clickCheck3 to 2
    ))
    }
    
**4. Functions**

Method                                                    | Description
----------------------------------------------------------|-------------------------------------------
`replaceList(dataSet: List<LT>)`                          | Replace the whole list
`replaceStartList(dataSet: List<LT>)`                     | Replace the list at the start
`replaceEndList(dataSet: List<LT>)`                       | Replace the list at the end
`replaceAfterPosList(dataSet: List<LT>, position: Int)`   | Replace the list after `position`
`deleteStartList(size: Int)`                              | Remove the list at start with size
`deleteEndList(size: Int)`                                | Remove the list at end with size
`deleteAfterPosList(size: Int, position: Int)`            | Remove the list with size after `position`
`prependList(dataSet: List<LT>)`                          | Add to start of list
`appendList(dataSet: List<LT>)`                           | Add to end of list
`addAfterPosList(dataSet: List<LT>, position: Int)`       | Add list after `position`
`prependItem(item: LT)`                                   | Add to start of item
`appendItem(item: LT)`                                    | Add to end of item
`addAfterPosItem(item: LT, position: Int)`                | Add item after `position`
`replaceStartItem(item: LT)`                              | Replace the item at the start
`replaceEndItem(item: LT)`                                | Replace the item at the end
`replaceAfterPosItem(item: LT, position: Int)`            | Replace the item after `position`
`deleteStartItem()`                                       | Remove the item at start
`deleteEndItem()`                                         | Remove the item at end
`deleteAfterPosItem(position: Int)`                       | Remove the item after `position`
__________________________________________________________|___________________________________
`getPosition(item: LT)`                                   | Get item position
`getPositions(list: List<LT>)`                            | Get list positions
`getHolder(item: LT)`                                     | Get item holder
`getHolder(position: Int)`                                | Get item holder by `position`
`getHolders(list: List<LT>)`                              | Get list holders
`getDataSet()`                                            | Get adapter data (list)
`getIdMap()`                                              | Get adapter map (item, postion, holder)
`updateHardAll()`                                         | notifyDataSetChanged()
`updateSoftAll()`                                         | notifyItemRangeChanged(0, `LIST SIZE`)
`updateItem(item: LT)`                                    | Update recycler element by item
`updateList(list: List<LT>)`                              | Update recycler elements by list
`updateStart(size: Int)`                                  | Update recycler elements at the start with `size`
`updateEnd(size: Int)`                                    | Update recycler elements at the end with `size`
`updateAfterPos(size: Int, position: Int)`                | Update recycler elements after `position` with `size`
`updateItemByParam(by: (LT) -> Boolean)`                  | Update recycler element by boolean expression
`clearData()`                                             | Clear whole list

## License ##

Licensed under the Apache License, see the [LICENSE](https://github.com/phlogistonCode/phrecycler/blob/master/LICENSE) for copying permission.
