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

Override setViewHolders:
- Use `vhClassToLayout(pairs)` for shorter and more convenient code entry.
- If You have one ViewHolder, then transfer only the pair with holder class to its layout id.

Example:

```kotlin
    class SamplePhrecycler(vararg clicks: (DataClass) -> Unit) : PhrecyclerAdapter<DataClass>(clicks) {

    override val setViewHolders = vhClassToLayout(Item1VH::class.java to R.layout.item_1)

    }
```

- If You have several ViewHolder, then transfer several classes of the holder to its layout id and override `setupViewType()`, write logic for return ViewHolder class.

Example:

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

Override bind() to bind code to a view.

To handle custom clicks, override the viewClicks() method and pass the map with the view to the number of the clicker function passed in the adapter implementation. (For example: click1: (DataClass) -> Unit - this is 0; click2: (DataClass) -> Unit - this is 1; click3: (DataClass) -> Unit - this is 2)

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
----------------------------------------------------------|-------------------------------------------
`replace(dataSet: List<LT>)`                              | Replace the whole list
`replaceStart(dataSet: List<LT>)`                     				| Replace the list at the start
`replaceEnd(dataSet: List<LT>)`                           | Replace the list at the end
`replace(dataSet: List<LT>, position: Int)`   												| Replace the list after `position`
`deleteStart(size: Int)`                                  | Remove the list at start with size
`deleteEnd(size: Int)`                                    | Remove the list at end with size
`delete(size: Int, position: Int)`                        | Remove the list with size after `position`
`prepend(dataSet: List<LT>)`                              | Add to start of list
`append(dataSet: List<LT>)`                               | Add to end of list
`add(dataSet: List<LT>, position: Int)`                   | Add list after `position`
`prepend(item: LT)`                                       | Add to start of item
`append(item: LT)`                                        | Add to end of item
`add(item: LT, position: Int)`                            | Add item after `position`
`replaceStart(item: LT)`                                  | Replace the item at the start
`replaceEnd(item: LT)`                                    | Replace the item at the end
`replace(item: LT, position: Int)`                        | Replace the item after `position`
`deleteStart()`                                           | Remove the item at start
`deleteEnd()`                                             | Remove the item at end
`delete(position: Int)`                                   | Remove the item after `position`
----------------------------------------------------------|-----------------------------------------
`getPosition(item: LT)`                                   | Get item position
`getPositions(list: List<LT>)`                            | Get list positions
`getHolder(item: LT)`                                     | Get item holder
`getHolder(position: Int)`                                | Get item holder by `position`
`getHolders(list: List<LT>)`                              | Get list holders
`getDataSet()`                                            | Get adapter data (list)
`getIdMap()`                                              | Get adapter map (item, postion, holder)
`updateHardAll()`                                         | notifyDataSetChanged()
`updateSoftAll()`                                         | notifyItemRangeChanged(0, `LIST SIZE`)
`update(item: LT)`                                        | Update recycler element by item
`update(list: List<LT>)`                                  | Update recycler elements by list
`updateStart(size: Int)`                                  | Update recycler elements at the start with `size`
`updateEnd(size: Int)`                                    | Update recycler elements at the end with `size`
`update(size: Int, position: Int)`                        | Update recycler elements after `position` with `size`
`update(by: (LT) -> Boolean)`                             | Update recycler element by boolean expression
`clearData()`                                             | Clear whole list

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
