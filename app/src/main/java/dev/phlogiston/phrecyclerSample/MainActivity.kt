package dev.phlogiston.phrecyclerSample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val adapter = SamplePhrecycler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prependData.setOnClickListener { adapter.prependItem(DataClass(Random.nextInt(30))) }
        appendData.setOnClickListener { adapter.appendItem(DataClass(Random.nextInt(30))) }
        anyAddData.setOnClickListener { adapter.addAfterPosItem(DataClass(Random.nextInt(30)), 3) }
        replaceStart.setOnClickListener { adapter.deleteAfterPosItem(9) }

        initRv()
    }

    private fun initRv() {
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        adapter.replaceList(listOf(
            DataClass(0),
            DataClass(1),
            DataClass(2),
            DataClass(3),
            DataClass(4),
            DataClass(5),
            DataClass(6),
            DataClass(7)
        ))
    }

}
