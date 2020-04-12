package dev.phlogiston.phrecycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier

abstract class PhrecyclerAdapter<LT>(private val funcs: Array<out (LT) -> Unit>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataSet: List<LT> = ArrayList()
    private var idMap: MutableMap<LT, Pair<Int, PhrecyclerViewHolder<LT>>> = mutableMapOf()

    abstract val setViewHolders: Map<Class<out PhrecyclerViewHolder<LT>>, Int>

    open fun setupViewType(): (LT) -> Class<out PhrecyclerViewHolder<LT>> =
        { setViewHolders.keys.first() }

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        setViewHolders.keys.find { getViewHolderType(it) == viewType }?.let {
            setViewHolders[it]?.let { layoutId ->
                createBaseGenericKInstance(it, createViewLayout(parent, layoutId))?.let { vh ->
                    when (vh) {
                        is PhrecyclerViewHolder<*> -> {
                            if (funcs.isNotEmpty()) {
                                if (vh.wholeClick != -1)
                                    vh.wholeClickListener = View.OnClickListener {
                                        funcs[vh.wholeClick](dataSet[vh.adapterPosition])
                                    }
                                vh.viewClicks.values.forEach { id ->
                                    vh.viewsClickListeners.add(View.OnClickListener {
                                        funcs[id](dataSet[vh.adapterPosition])
                                    })
                                }
                                vh.createClickListeners()
                            }
                        }
                    }
                    return vh
                } ?: return createBaseViewHolder(parent)
            } ?: return createBaseViewHolder(parent)
        } ?: return createBaseViewHolder(parent)
    }

    final override fun getItemViewType(position: Int) =
        if (setViewHolders.keys.size == 1) setViewHolders.getValue(setViewHolders.keys.first())
        else getViewHolderType(setupViewType().invoke(dataSet[position]))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.tryCast<PhrecyclerViewHolder<LT>> {
            this.bind(dataSet[position])
            idMap[dataSet[position]] = Pair(this.adapterPosition, this)
        }
    }

    override fun getItemCount() = dataSet.size

    fun append(item: LT) {
        this.dataSet = this.dataSet + item
        notifyItemRangeInserted(this.dataSet.size - 1, 1)
    }

    fun append(dataSet: List<LT>) {
        this.dataSet = this.dataSet + dataSet
        notifyItemRangeInserted(this.dataSet.size, dataSet.size)
    }

    fun prepend(item: LT) {
        this.dataSet = listOf(item) + this.dataSet
        notifyItemRangeInserted(0, 1)
    }

    fun prepend(dataSet: List<LT>) {
        this.dataSet.prepend(dataSet)
        notifyItemRangeInserted(0, dataSet.size)
    }

    fun add(item: LT, position: Int) {
        if (position < 0) {
            this.dataSet.addAfter(item, 0)
            notifyItemRangeInserted(0, 1)
        } else {
            this.dataSet.addAfter(item, position)
            notifyItemRangeInserted(position, 1)
        }
    }

    fun add(dataSet: List<LT>, position: Int) {
        if (position < 0) {
            this.dataSet.addAfter(dataSet, 0)
            notifyItemRangeInserted(0, dataSet.size)
        } else {
            this.dataSet.addAfter(dataSet, position)
            notifyItemRangeInserted(position, dataSet.size)
        }
    }

    fun addAfterItem(item: LT, by: LT.() -> Boolean) {
        val findedPos = dataSet.find(by)?.let { getPosition(it) }
        findedPos?.let { add(item, it + 1) }
    }

    fun addAfterItem(dataSet: List<LT>, by: LT.() -> Boolean) {
        val findedPos = dataSet.find(by)?.let { getPosition(it) }
        findedPos?.let { add(dataSet, it + 1) }
    }

    fun addBeforeItem(item: LT, by: LT.() -> Boolean) {
        val findedPos = dataSet.find(by)?.let { getPosition(it) }
        findedPos?.let { add(item, it) }
    }

    fun addBeforeItem(dataSet: List<LT>, by: LT.() -> Boolean) {
        val findedPos = dataSet.find(by)?.let { getPosition(it) }
        findedPos?.let { add(dataSet, it) }
    }

    fun replace(dataSet: List<LT>) {
        this.dataSet = dataSet
        notifyItemRangeChanged(0, dataSet.size)
    }

    fun replace(item: LT, position: Int) {
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

    fun replace(dataSet: List<LT>, position: Int) {
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

    fun replace(item: LT, by: LT.() -> Boolean) {
        val findedPos = dataSet.find(by)?.let { getPosition(it) }
        findedPos?.let { replace(item, it) }
    }

    fun replace(dataSet: List<LT>, by: LT.() -> Boolean) {
        val findedPos = dataSet.find(by)?.let { getPosition(it) }
        findedPos?.let { replace(dataSet, it) }
    }

    fun replaceStart(item: LT) {
        this.dataSet.replaceStart(item)
        notifyItemChanged(0)
    }

    fun replaceStart(dataSet: List<LT>) {
        this.dataSet.replaceStart(dataSet)
        notifyItemRangeChanged(0, dataSet.size)
    }

    fun replaceEnd(item: LT) {
        this.dataSet.replaceEnd(item)
        notifyItemChanged(this.dataSet.size - 1)
    }

    fun replaceEnd(dataSet: List<LT>) {
        this.dataSet.replaceEnd(dataSet)
        notifyItemRangeChanged(this.dataSet.size - dataSet.size, dataSet.size)
    }

    fun deleteStart() {
        this.dataSet = dataSet.drop(1)
        notifyItemRemoved(0)
    }

    fun deleteStart(size: Int) {
        this.dataSet = dataSet.drop(size)
        notifyItemRangeRemoved(0, size)
    }

    fun deleteEnd() {
        this.dataSet = dataSet.dropLast(1)
        notifyItemRemoved(this.dataSet.size)
    }

    fun deleteEnd(size: Int) {
        if (size >= this.dataSet.size) clearData()
        else {
            this.dataSet = dataSet.dropLast(size)
            notifyItemRangeRemoved(this.dataSet.size, size)
        }
    }

    fun delete(position: Int) {
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

    fun delete(size: Int, position: Int) {
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

    fun delete(item: LT) {
        val pos = getPosition(item)
        dataSet = dataSet - item
        pos?.let { notifyItemRemoved(it) }
    }

    fun delete(by: LT.() -> Boolean) {
        dataSet.find(by)?.let { delete(it) }
    }

    fun clearData() {
        val tempSize = dataSet.size
        dataSet = emptyList()
        notifyItemRangeRemoved(0, tempSize)
    }

    fun getPosition(item: LT) = idMap[item]?.first

    fun getPosition(by: LT.() -> Boolean) {
        dataSet.find(by)?.let { getPosition(it) }
    }

    fun getPositions(list: List<LT>): List<Int> {
        val positions = mutableListOf<Int>()
        list.forEach {
            idMap[it]?.first?.let { position -> positions.add(position) }
        }
        return positions
    }

    fun getScreenPositions(layoutManager: RecyclerView.LayoutManager?): List<Int?> {
        return getPositions(getScreenItems(layoutManager))
    }

    fun getScreenPosition(layoutManager: RecyclerView.LayoutManager?, by: LT.() -> Boolean): Int? {
        return getScreenItem(layoutManager, by)?.let { getPosition(it) }
    }

    fun getScreenPositionFirst(layoutManager: RecyclerView.LayoutManager?): Int? {
        return getScreenItemFirst(layoutManager)?.let { getPosition(it) }
    }

    fun getScreenPositionLast(layoutManager: RecyclerView.LayoutManager?): Int? {
        return getScreenItemLast(layoutManager)?.let { getPosition(it) }
    }

    fun getHolder(item: LT) = idMap[item]?.second

    fun getHolder(position: Int) = idMap[dataSet[position]]?.second

    fun getHolder(by: LT.() -> Boolean) {
        dataSet.find(by)?.let { getHolder(it) }
    }

    fun getHolders(list: List<LT>): List<RecyclerView.ViewHolder> {
        val holders = mutableListOf<RecyclerView.ViewHolder>()
        list.forEach {
            idMap[it]?.second?.let { holder -> holders.add(holder) }
        }
        return holders
    }

    fun getScreenHolders(layoutManager: RecyclerView.LayoutManager?): List<RecyclerView.ViewHolder?> {
        return getHolders(getScreenItems(layoutManager))
    }

    fun getScreenHolder(layoutManager: RecyclerView.LayoutManager?, by: LT.() -> Boolean): RecyclerView.ViewHolder? {
        return getScreenItem(layoutManager, by)?.let {getHolder(it) }
    }

    fun getScreenHolderFirst(layoutManager: RecyclerView.LayoutManager?): RecyclerView.ViewHolder? {
        return getScreenItemFirst(layoutManager)?.let { getHolder(it) }
    }

    fun getScreenHolderLast(layoutManager: RecyclerView.LayoutManager?): RecyclerView.ViewHolder? {
        return getScreenItemLast(layoutManager)?.let { getHolder(it) }
    }

    fun update(item: LT) = getPosition(item)?.let { notifyItemChanged(it) }

    fun update(list: List<LT>) {
        list.forEach { item ->
            getPosition(item)?.let { notifyItemChanged(it) }
        }
    }

    fun update(size: Int, position: Int) = notifyItemRangeChanged(position, size)

    fun update(by: LT.() -> Boolean) {
        idMap[idMap.keys.find(by)]?.let { notifyItemChanged(it.first) }
    }

    fun updateStart(size: Int) = notifyItemRangeChanged(0, size)

    fun updateEnd(size: Int) = notifyItemRangeChanged(dataSet.size - size, size)

    fun updateScreen(layoutManager: RecyclerView.LayoutManager?) {
        update(getScreenItems(layoutManager))
    }

    fun updateScreen(layoutManager: RecyclerView.LayoutManager?, by: LT.() -> Boolean) {
        getScreenItem(layoutManager, by)?.let { update(it) }
    }

    fun updateScreenFirst(layoutManager: RecyclerView.LayoutManager?) {
        getScreenItemFirst(layoutManager)?.let { update(it) }
    }

    fun updateScreenLast(layoutManager: RecyclerView.LayoutManager?) {
        getScreenItemLast(layoutManager)?.let { update(it) }
    }

    fun updateSoftAll() = notifyItemRangeChanged(0, dataSet.size)

    fun updateHardAll() = notifyDataSetChanged()

    fun getItem(position: Int) = dataSet[position]

    fun getFirstItem() = dataSet.first()

    fun getLastItem() = dataSet.last()

    fun getItem(by: LT.() -> Boolean) = dataSet.find(by)

    fun getItems(by: LT.() -> Boolean) = dataSet.filter(by)

    fun getScreenItems(layoutManager: RecyclerView.LayoutManager?): List<LT> {
        return when(layoutManager) {
            is LinearLayoutManager -> {
                val firstId = layoutManager.findFirstVisibleItemPosition()
                val lastId = layoutManager.findLastVisibleItemPosition()
                dataSet.subList(firstId, lastId)
            }
            is GridLayoutManager -> {
                val firstId = layoutManager.findFirstVisibleItemPosition()
                val lastId = layoutManager.findLastVisibleItemPosition()
                dataSet.subList(firstId, lastId)
            }
            else -> listOf()
        }
    }

    fun getScreenItem(layoutManager: RecyclerView.LayoutManager?, by: LT.() -> Boolean): LT? {
        return when(layoutManager) {
            is LinearLayoutManager -> {
                val firstId = layoutManager.findFirstVisibleItemPosition()
                val lastId = layoutManager.findLastVisibleItemPosition()
                dataSet.subList(firstId, lastId).find(by)
            }
            is GridLayoutManager -> {
                val firstId = layoutManager.findFirstVisibleItemPosition()
                val lastId = layoutManager.findLastVisibleItemPosition()
                dataSet.subList(firstId, lastId).find(by)
            }
            else -> null
        }
    }

    fun getScreenItemFirst(layoutManager: RecyclerView.LayoutManager?): LT? {
        return when(layoutManager) {
            is LinearLayoutManager -> {
                val firstId = layoutManager.findFirstVisibleItemPosition()
                dataSet[firstId]
            }
            is GridLayoutManager -> {
                val firstId = layoutManager.findFirstVisibleItemPosition()
                dataSet[firstId]
            }
            else -> null
        }
    }

    fun getScreenItemLast(layoutManager: RecyclerView.LayoutManager?): LT? {
        return when(layoutManager) {
            is LinearLayoutManager -> {
                val lastId = layoutManager.findLastVisibleItemPosition()
                dataSet[lastId]
            }
            is GridLayoutManager -> {
                val lastId = layoutManager.findLastVisibleItemPosition()
                dataSet[lastId]
            }
            else -> null
        }
    }

    fun getDataSet() = this.dataSet

    fun getIdMap() = this.idMap

    fun change(item: LT, change: LT.() -> Unit) {
        item.changeItemInternal(change)
    }

    fun vhClassToLayout(vararg pairs: Pair<Class<out PhrecyclerViewHolder<LT>>, Int>)
            : Map<Class<out PhrecyclerViewHolder<LT>>, Int> = mapOf(*pairs)

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

    private fun LT.changeItemInternal(change: LT.() -> Unit) {
        val pos = getPosition(this)
        this.change()
        pos?.let { notifyItemChanged(it) }
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
