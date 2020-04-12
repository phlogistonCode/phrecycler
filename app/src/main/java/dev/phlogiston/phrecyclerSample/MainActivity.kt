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

        prependData.setOnClickListener { adapter.prepend(DataClass(Random.nextInt(30))) }
        appendData.setOnClickListener { adapter.append(DataClass(Random.nextInt(30))) }
        anyAddData.setOnClickListener { adapter.add(DataClass(Random.nextInt(30)), 3) }
        replaceStart.setOnClickListener { adapter.replace(DataClass(5), 2) }
        additBtn.setOnClickListener {
            recycler.smoothScrollToPosition(100)
        }

        dop1.setOnClickListener { adapter.updateStart(3) }
        dop2.setOnClickListener { adapter.updateEnd(3) }
        dop3.setOnClickListener { adapter.update(3, 3) }
        dop4.setOnClickListener { adapter.updateSoftAll() }
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
