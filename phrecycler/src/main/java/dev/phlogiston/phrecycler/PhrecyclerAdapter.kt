package dev.phlogiston.phrecycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier

abstract class PhrecyclerAdapter<LT>(private vararg val funcs: (LT) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataSet: List<LT> = ArrayList()
    private var idMap: MutableMap<LT, Pair<Int, RecyclerView.ViewHolder>> = mutableMapOf()

    abstract val setViewHolders: Map<Class<out PhrecyclerViewHolder<LT>>, Int>

    open fun determineVTFunc(): (LT) -> Class<out RecyclerView.ViewHolder> =
        { setViewHolders.keys.first() }

    open fun itemClick(): (LT) -> Unit = {}

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        setViewHolders.keys.find { getViewHolderType(it) == viewType }?.let {
            setViewHolders[it]?.let { layoutId ->
                createBaseGenericKInstance(it, createViewLayout(parent, layoutId))?.let { vh ->
                    return vh
                } ?: return createBaseViewHolder(parent)
            } ?: return createBaseViewHolder(parent)
        } ?: return createBaseViewHolder(parent)
    }

    final override fun getItemViewType(position: Int) =
        if (setViewHolders.keys.size == 1) setViewHolders.getValue(setViewHolders.keys.first())
        else getViewHolderType(determineVTFunc().invoke(dataSet[position]))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.tryCast<PhrecyclerViewHolder<LT>> {
            this.bind(dataSet[position])
            idMap[dataSet[position]] = Pair(this.adapterPosition, this)
            this.itemView.setOnClickListener { itemClick().invoke(dataSet[position]) }
            if (funcs.isNotEmpty())
                setViewHolders.keys.forEach { clazz ->
                    if (clazz == this.viewClick().second) {
                        this.viewClick().first.keys.forEach { view ->
                            this.viewClick().first[view]?.let { funcId ->
                                view?.setOnClickListener { funcs[funcId].invoke(dataSet[position]) }
                            }
                        }
                    }
                }
        }
    }

    override fun getItemCount() = dataSet.size

    fun replaceList(dataSet: List<LT>) {
        this.dataSet = dataSet
        notifyItemRangeChanged(0, dataSet.size)
    }

    fun replaceStartList(dataSet: List<LT>) {
        this.dataSet.replaceStart(dataSet)
        notifyItemRangeChanged(0, dataSet.size)
    }

    fun replaceEndList(dataSet: List<LT>) {
        this.dataSet.replaceEnd(dataSet)
        notifyItemRangeChanged(this.dataSet.size - dataSet.size, dataSet.size)
    }

    fun replaceAfterPosList(dataSet: List<LT>, position: Int) {
        if (position < 0) {
            this.dataSet.replaceStart(dataSet)
            notifyItemRangeChanged(0, dataSet.size)
        } else {
            this.dataSet.replaceAfterPos(dataSet, position)
            if (position >= this.dataSet.size || this.dataSet.size - position <= dataSet.size)
                notifyItemRangeChanged(this.dataSet.size - dataSet.size, dataSet.size)
            else notifyItemRangeChanged(position, dataSet.size)
        }
    }

    fun deleteStartList(size: Int) {
        this.dataSet = dataSet.drop(size)
        notifyItemRangeRemoved(0, size)
    }

    fun deleteEndList(size: Int) {
        if (size >= this.dataSet.size) clearData()
        else {
            this.dataSet = dataSet.dropLast(size)
            notifyItemRangeRemoved(this.dataSet.size, size)
        }
    }

    fun deleteAfterPosList(size: Int, position: Int) {
        if (position < 0) {
            if (this.dataSet.isNotEmpty()) {
                if (this.dataSet.size < size) {
                    clearData()
                } else {
                    this.dataSet = this.dataSet.drop(size)
                    notifyItemRangeRemoved(0, size)
                }
            }
        } else {
            if (position < this.dataSet.size) {
                if (this.dataSet.size - position < size) {
                    val newSize = this.dataSet.size - position
                    this.dataSet = this.dataSet.dropLast(newSize)
                    notifyItemRangeRemoved(this.dataSet.size, newSize)
                } else {
                    val tempList = this.dataSet.split(position)
                    this.dataSet = tempList.first
                    this.dataSet = this.dataSet + tempList.second.drop(size)
                    notifyItemRangeRemoved(tempList.first.size, size)
                }
            }
        }
    }

    fun addAfterPosList(dataSet: List<LT>, position: Int) {
        if (position < 0) {
            this.dataSet.addAfter(dataSet, 0)
            notifyItemRangeInserted(0, dataSet.size)
        } else {
            this.dataSet.addAfter(dataSet, position)
            notifyItemRangeInserted(position, dataSet.size)
        }
    }

    fun appendList(dataSet: List<LT>) {
        this.dataSet = this.dataSet + dataSet
        notifyItemRangeInserted(this.dataSet.size, dataSet.size)
    }

    fun prependList(dataSet: List<LT>) {
        this.dataSet.prepend(dataSet)
        notifyItemRangeInserted(0, dataSet.size)
    }

    fun prependItem(item: LT) {
        this.dataSet = listOf(item) + this.dataSet
        notifyItemRangeInserted(0, 1)
    }

    fun appendItem(item: LT) {
        this.dataSet = this.dataSet + item
        notifyItemRangeInserted(this.dataSet.size - 1, 1)
    }

    fun addAfterPosItem(item: LT, position: Int) {
        if (position < 0) {
            this.dataSet.addAfter(item, 0)
            notifyItemRangeInserted(0, 1)
        } else {
            this.dataSet.addAfter(item, position)
            notifyItemRangeInserted(position, 1)
        }
    }

    fun replaceStartItem(item: LT) {
        this.dataSet.replaceStart(item)
        notifyItemChanged(0)
    }

    fun replaceEndItem(item: LT) {
        this.dataSet.replaceEnd(item)
        notifyItemChanged(this.dataSet.size - 1)
    }

    fun replaceAfterPosItem(item: LT, position: Int) {
        if (position < 0) {
            this.dataSet.replaceStart(item)
            notifyItemChanged(0)
        } else {
            if (position < this.dataSet.size) {
                this.dataSet.replaceAfterPos(item, position)
                notifyItemChanged(position)
            }
        }
    }

    fun deleteStartItem() {
        this.dataSet = dataSet.drop(1)
        notifyItemRemoved(0)
    }

    fun deleteEndItem() {
        this.dataSet = dataSet.dropLast(1)
        notifyItemRemoved(this.dataSet.size)
    }

    fun deleteAfterPosItem(position: Int) {
        if (position < 0) {
            if (this.dataSet.isNotEmpty()) {
                this.dataSet = dataSet.drop(1)
                notifyItemRemoved(0)
            }
        } else {
            if (position < this.dataSet.size) {
                val tempList = this.dataSet.split(position)
                this.dataSet = tempList.first
                this.dataSet = this.dataSet + tempList.second.drop(1)
                notifyItemRemoved(tempList.first.size)
            }
        }
    }

    fun getPosition(item: LT) = idMap[item]?.first

    fun getPositions(list: List<LT>): List<Int> {
        val positions = mutableListOf<Int>()
        list.forEach {
            idMap[it]?.first?.let { position -> positions.add(position) }
        }
        return positions
    }

    fun getHolder(item: LT) = idMap[item]?.second

    fun getHolder(position: Int) = idMap[dataSet[position]]?.second

    fun getHolders(list: List<LT>): List<RecyclerView.ViewHolder> {
        val holders = mutableListOf<RecyclerView.ViewHolder>()
        list.forEach {
            idMap[it]?.second?.let { holder -> holders.add(holder) }
        }
        return holders
    }

    fun getDataSet() = this.dataSet

    fun getIdMap() = this.idMap

    fun updateHardAll() = notifyDataSetChanged()

    fun updateSoftAll() = notifyItemRangeChanged(0 , dataSet.size)

    fun updateItem(item: LT) = getPosition(item)?.let { notifyItemChanged(it) }

    fun updateList(list: List<LT>) {
        list.forEach { item ->
            getPosition(item)?.let { notifyItemChanged(it) }
        }
    }

    fun updateStart(size: Int) = notifyItemRangeChanged(0, size)

    fun updateEnd(size: Int) = notifyItemRangeChanged(dataSet.size - size, size)

    fun updateAfterPos(size: Int, position: Int) = notifyItemRangeChanged(position, size)

    fun updateItemByParam(by: (LT) -> Boolean) {
        idMap[idMap.keys.find { by.invoke(it) }]?.let { notifyItemChanged(it.first) }
    }

    fun clearData() {
        val tempSize = dataSet.size
        dataSet = emptyList()
        notifyItemRangeRemoved(0, tempSize)
    }

    private fun createBaseViewHolder(parent: ViewGroup) =
        BaseViewHolder(createViewLayout(parent, R.layout.item_baseviewholder))

    private fun createViewLayout(parent: ViewGroup, @LayoutRes layoutResId: Int) =
        LayoutInflater.from(parent.context).inflate(
            layoutResId,
            parent,
            false
        )

    private fun getViewHolderType(viewHolderClass: Class<*>) = viewHolderClass.hashCode()

    private fun createBaseGenericKInstance(z: Class<*>, view: View): RecyclerView.ViewHolder? {
        try {
            val constructor: Constructor<*>
            // inner and unstatic class
            return if (z.isMemberClass && !Modifier.isStatic(z.modifiers)) {
                constructor = z.getDeclaredConstructor(javaClass, View::class.java)
                constructor.isAccessible = true
                constructor.newInstance(this, view) as RecyclerView.ViewHolder
            } else {
                constructor = z.getDeclaredConstructor(View::class.java)
                constructor.isAccessible = true
                constructor.newInstance(view) as RecyclerView.ViewHolder
            }
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

        return null
    }

    private inline fun <reified T> Any?.tryCast(block: T.() -> Unit) {
        if (this is T) {
            block()
        }
    }

    private fun List<LT>.prepend(list: List<LT>) {
        val tempList = this
        dataSet = list
        dataSet = dataSet + tempList
    }

    private fun List<LT>.addAfter(list: List<LT>, position: Int) {
        if (position < this.size) {
            val tempList = this.split(position)
            dataSet = tempList.first
            dataSet = dataSet + list
            dataSet = dataSet + tempList.second
        } else {
            dataSet = dataSet + list
        }
    }

    private fun List<LT>.addAfter(item: LT, position: Int) {
        if (position < this.size) {
            val tempList = this.split(position)
            dataSet = tempList.first
            dataSet = dataSet + item
            dataSet = dataSet + tempList.second
        } else {
            dataSet = dataSet + item
        }
    }

    private fun List<LT>.replaceStart(list: List<LT>) {
        if (this.size <= list.size) {
            dataSet = list
        } else {
            val tempList = this.drop(list.size)
            dataSet = list
            dataSet = dataSet + tempList
        }
    }

    private fun List<LT>.replaceEnd(list: List<LT>) {
        if (this.size <= list.size) {
            dataSet = list
        } else {
            val tempList = this.take(this.size - list.size)
            dataSet = tempList
            dataSet = dataSet + list
        }
    }

    private fun List<LT>.replaceAfterPos(list: List<LT>, position: Int) {
        if (this.size <= list.size) {
            dataSet = list
        } else {
            if (position < this.size && this.size - position > list.size) {
                val tempList = this.split(position)
                dataSet = tempList.first
                dataSet = dataSet + list
                dataSet = dataSet + tempList.second.drop(list.size)
            } else {
                val tempList = this.take(this.size - list.size)
                dataSet = tempList
                dataSet = dataSet + list
            }

        }
    }

    private fun List<LT>.replaceStart(item: LT) {
        if (this.size <= 1) {
            dataSet = listOf(item)
        } else {
            val tempList = this.drop(1)
            dataSet = listOf(item)
            dataSet = dataSet + tempList
        }
    }

    private fun List<LT>.replaceEnd(item: LT) {
        if (this.size <= 1) {
            dataSet = listOf(item)
        } else {
            val tempList = this.take(this.size - 1)
            dataSet = tempList
            dataSet = dataSet + item
        }
    }

    private fun List<LT>.replaceAfterPos(item: LT, position: Int) {
        if (this.size <= 1) {
            dataSet = listOf(item)
        } else {
            if (position < this.size) {
                val tempList = this.split(position)
                dataSet = tempList.first
                dataSet = dataSet + item
                dataSet = dataSet + tempList.second.drop(1)
            }

        }
    }

}
