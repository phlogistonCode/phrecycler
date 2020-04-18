package dev.phlogiston.phrecyclerSample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dev.phlogiston.phrecycler.ScrollerLinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val adapter = SamplePhrecycler(
        { Log.d("PHRECYCLER: ", "ITEM ID: ${it.id}") },
        { Log.d("PHRECYCLER: ", "CLICK1 ID: ${it.id}") },
        { Log.d("PHRECYCLER: ", "CLICK2 ID: ${it.id}") },
        { Log.d("PHRECYCLER: ", "CLICK3 ID: ${it.id}") }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prependData.setOnClickListener { adapter.addAfterItem(DataClass(Random.nextInt(30))) { id == 3 } }
        appendData.setOnClickListener { adapter.addAfterItem(listOf(DataClass(Random.nextInt(30)), DataClass(Random.nextInt(30)))) { id == 3 } }
        anyAddData.setOnClickListener { adapter.replace(DataClass(Random.nextInt(30))) { id == 4 } }
        replaceStart.setOnClickListener { adapter.replace(listOf(DataClass(Random.nextInt(30)), DataClass(Random.nextInt(30)), DataClass(Random.nextInt(30)))) { id == 5 } }
        additBtn.setOnClickListener { Log.d("POS: ", adapter.getScreenPositions(recycler.layoutManager).toString()) }

        dop1.setOnClickListener { adapter.append(DataClass(3)) }
        dop2.setOnClickListener { adapter.update(DataClass(100)) }
        dop3.setOnClickListener { adapter.update(listOf(DataClass(100), DataClass(4))) }
        dop4.setOnClickListener { adapter.update { id == 100 } }
        dop5.setOnClickListener { adapter.updateHardAll() }

        initRv()
    }

    private fun initRv() {
        recycler.layoutManager = ScrollerLinearLayoutManager(this)
        recycler.adapter = adapter
        val list = mutableListOf<DataClass>()
        for (i in 0..500) {
            list.add(DataClass(i))
        }
        adapter.replace(list)
    }

}
