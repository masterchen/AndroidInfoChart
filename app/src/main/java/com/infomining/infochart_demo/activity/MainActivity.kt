package com.infomining.infochart_demo.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.infomining.infochart_demo.adapter.ChartListAdapter
import com.infomining.infochart_demo.model.ChartListModel
import com.infomining.infochart_demo.R

class MainActivity : BaseActivity(), ChartListAdapter.OnListItemClickListener {

    lateinit var listChart: RecyclerView
    lateinit var adapter: ChartListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewInit()
    }

    private fun viewInit() {
        listChart = findViewById(R.id.list_chart)
        adapter = ChartListAdapter(listener = this)

        listChart.adapter = adapter
        listChart.layoutManager = LinearLayoutManager(this)

        setListView()
    }

    private fun setListView() {
        // #1 RealTime Chart
        ResourcesCompat.getDrawable(resources, R.drawable.heart_pulse, null)
                ?.let {
                    ChartListModel(1, "RealTime Vital Chart", "실시간 ECG 그래프", it)
                }
                ?.let {
                    adapter.addList(it)
                }
    }

    override fun onListItemClick(id: Int) {

        val intent: Intent? = when(id) {
            1 -> {
                Intent(this, RealTimeChartActivity::class.java)
            }
            else -> {
                null
            }
        }

        intent?.let {
            startActivity(it)
            overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity)
        }



    }

}