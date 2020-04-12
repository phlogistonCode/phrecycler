# phrecycler

This is a library that simplifies working with Recycler View.

Android API：minimum support for Android **4.0** (API 14)

**Features**

 - Use different types of holders.
 - Implement selectors - easy!
 - Do anything with the list: add, delete, replace.
 - Have access to the position and holder of any item.
 - Update the modified list in any convenient way.
 - Easy scroll your phrecycler to any element.
 
 **In dev**
- Handling an empty list.
- Adapter animations.
- Header and footer.
- Drag-slide actions.
- Load more actions.
- Improve memory consumption.

**1. Gradle**

    implementation 'dev.phlogistoncode:phrecycler:1.1'

**2. PhrecyclerAdapter**

Pass in the type of your data class and, if necessary, a list of lambdas to handle clicks.

Override setViewHolders:
- Use `vhClassToLayout(pairs)` for shorter and more convenient code entry.
- If You have one ViewHolder, then transfer only the pair with holder class to its layout id:

```kotlin
    class SamplePhrecycler(vararg clicks: (DataClass) -> Unit) : PhrecyclerAdapter<DataClass>(clicks) {

    override val setViewHolders = vhClassToLayout(Item1VH::class.java to R.layout.item_1)

    }
```

- If You have several ViewHolder, then transfer several classes of the holder to its layout id and override `setupViewType()`, write logic for return ViewHolder class:

```kotlin
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
```

**3. PhrecyclerViewHolder**

Pass in the type of your data class.

Override `bind()` to bind code to a view.

To handle clicks throughout the Viewholder, override `wholeClick` and pass the ID from the list of lambdas defined in the adapter.

To handle custom clicks, override the `viewClicks` variable and pass the pairs with the view to the ID of the clicker function defined in the adapter implementation.
<br> For example: First element of the `vararg clicks: (DataClass) -> Unit` - this is 0; Second - this is 1; Third - this is 2.
<br>Use `viewToFuncId(pairs)` for shorter and more convenient code entry.

```kotlin
    class Item1VH(itemView: View) : PhrecyclerViewHolder<DataClass>(itemView) {

    override fun bind(item: DataClass) = with(itemView) {
        text.text = "ITEM1: ${item.id}"
    }

    override val wholeClick = 0
    
    override val viewClicks = viewToFuncId(itemView.clickCheck1 to 1)

    }
 ```
 ________________________________________________________________________________
    
```kotlin
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
```
    
**4. Functions**

Method                                                    | Description
----------------------------------------------------------|----------------------------------------------------------------------
**Adding**                                                | 
`append(item: LT)`                                        | Add to end of item
`append(dataSet: List<LT>)`                               | Add to end of list
`prepend(item: LT)`                                       | Add to start of item
`prepend(dataSet: List<LT>)`                              | Add to start of list
`add(item: LT, position: Int)`                            | Add item after `position`
`add(dataSet: List<LT>, position: Int)`                   | Add list after `position`
`addAfterItem(item: LT, by: LT.() -> Boolean)`            | Add item after item found by parameter
`addAfterItem(dataSet: List<LT>, by: LT.() -> Boolean)`   | Add list after item found by parameter
`addBeforeItem(item: LT, by: LT.() -> Boolean)`           | Add item before item found by parameter
`addBeforeItem(dataSet: List<LT>, by: LT.() -> Boolean)`  | Add list before item found by parameter
**Replacing**                                             | 
`replace(dataSet: List<LT>)`                              | Replace the whole list
`replace(item: LT, position: Int)`                        | Replace the item in `position`
`replace(dataSet: List<LT>, position: Int)`   												| Replace the list in `position`
`replace(item: LT, by: LT.() -> Boolean)`                 | Replace the item found by parameter
`replace(dataSet: List<LT>, by: LT.() -> Boolean)`        | Replace the list starting with the element found by parameter
`replaceStart(item: LT)`                                  | Replace the item at the start
`replaceStart(dataSet: List<LT>)`                     				| Replace the list at the start
`replaceEnd(item: LT)`                                    | Replace the item at the end
`replaceEnd(dataSet: List<LT>)`                           | Replace the list at the end
**Removing**                                              | 
`deleteStart()`                                           | Remove the item at start
`deleteStart(size: Int)`                                  | Remove the list at start with size
`deleteEnd()`                                             | Remove the item at end
`deleteEnd(size: Int)`                                    | Remove the list at end with size
`delete(position: Int)`                                   | Remove the item after `position`
`delete(size: Int, position: Int)`                        | Remove the list with size after `position`
`delete(item: LT)`                                        | Remove the item
`delete(by: LT.() -> Boolean)`                            | Remove the item found by parameter
`clearData()`                                             | Clear whole list
**Get position**                                          | 
`getPosition(item: LT)`                                   | Get item position
`getPosition(by: LT.() -> Boolean)`                       | Get position item found by parameter
`getPositions(list: List<LT>)`                            | Get list positions
`getScreenPositions(layoutManager)`                       | Pass layout manager to get items positions from screen
`getScreenPosition(layoutManager, by: LT.() -> Boolean)`  | Get item position by parameter from screen
`getScreenPositionFirst(layoutManager)`                   | Get position first item on screen
`getScreenPositionLast(layoutManager)`                    | Get position last item on screen
**Get holder**                                            | 
`getHolder(item: LT)`                                     | Get item holder
`getHolder(position: Int)`                                | Get item holder by `position`
`getHolder(by: LT.() -> Boolean)`                         | Get holder item found by parameter
`getHolders(list: List<LT>)`                              | Get list holders
`getScreenHolders(layoutManager)`                         | Pass layout manager to get items holders from screen
`getScreenHolder(layoutManager, by: LT.() -> Boolean)`    | Get item holder by parameter from screen
`getScreenHolderFirst(layoutManager)`                     | Get holder first item on screen
`getScreenHolderLast(layoutManager)`                      | Get holder last item on screen
**Updating**                                              | 
`update(item: LT)`                                        | Update recycler element by item
`update(list: List<LT>)`                                  | Update recycler elements by list
`update(size: Int, position: Int)`                        | Update recycler elements after `position` with `size`
`update(by: (LT) -> Boolean)`                             | Update recycler element by parameter
`updateStart(size: Int)`                                  | Update recycler elements at the start with `size`
`updateEnd(size: Int)`                                    | Update recycler elements at the end with `size`
`updateScreen(layoutManager)`                             | Pass layout manager to update all items on screen
`updateScreen(layoutManager, by: LT.() -> Boolean)`       | Update item on screen by parameter
`updateScreenFirst(layoutManager)`                        | Update first item on screen
`updateScreenLast(layoutManager)`                         | Update last item on screen
`updateSoftAll()`                                         | notifyItemRangeChanged(0, `LIST SIZE`)
`updateHardAll()`                                         | notifyDataSetChanged()
**Get item**                                              |
`getItem(position: Int)`                                  | Get item by `position`
`getFirstItem()`                                          | Get first item
`getLastItem()`                                           | Get last item
`getItem(by: LT.() -> Boolean)`                           | Get item by parameter
`getItems(by: LT.() -> Boolean)`                          | Get items by parameter
`getScreenItems(layoutManager)`                           | Get items on screen
`getScreenItem(layoutManager, by: LT.() -> Boolean)`      | Get item on screen by parameter
`getScreenItemFirst(layoutManager)`                       | Get first item on screen
`getScreenItemLast(layoutManager)`                        | Get last item on screen
`getDataSet()`                                            | Get adapter data (list)
**Other**                                                 | 
`getIdMap()`                                              | Get adapter map (item, postion, holder)
`change(item: LT, change: LT.() -> Unit)`                 | Change item\`s parameters

**5. Scroll To**

If you use the LinearLayoutManager, then you can use the ScrollerLinearLayoutManager:

```kotlin 
recycler.layoutManager = ScrollerLinearLayoutManager(this)
```

Then you can call `smoothScrollToPosition(position)` to smoothly scroll to the element (the element will be at the top of the screen):
```kotlin
recycler.smoothScrollToPosition(100)
```

## License ##
```
Copyright 2020 Alexander Popov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

See the [LICENSE](https://github.com/phlogistonCode/phrecycler/blob/master/LICENSE) for copying permission.
