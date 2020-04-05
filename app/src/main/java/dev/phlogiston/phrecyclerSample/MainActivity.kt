package dev.phlogiston.phrecyclerSample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

        prependData.setOnClickListener { adapter.prependItem(DataClass(Random.nextInt(30))) }
        appendData.setOnClickListener { adapter.appendItem(DataClass(Random.nextInt(30))) }
        anyAddData.setOnClickListener { adapter.addAfterPosItem(DataClass(Random.nextInt(30)), 3) }
        replaceStart.setOnClickListener { adapter.deleteAfterPosItem(2) }
        additBtn.setOnClickListener {
            Log.d("PHRECYCLER: ", adapter.getHolder(5).toString())
            Log.d("PHRECYCLER: ", adapter.getPosition(DataClass(5)).toString())
        }

        dop1.setOnClickListener { adapter.updateStart(3) }
        dop2.setOnClickListener { adapter.updateEnd(3) }
        dop3.setOnClickListener { adapter.updateAfterPos(3, 3) }
        dop4.setOnClickListener { adapter.updateSoftAll() }
        dop5.setOnClickListener { adapter.updateHardAll() }

        initRv()
    }

    private fun initRv() {
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        val list = mutableListOf<DataClass>()
        for (i in 0..50) {
            list.add(DataClass(Random.nextInt(30)))
        }
        adapter.replaceList(list)
    }

}
