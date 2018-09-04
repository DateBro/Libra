package com.example.thinkpad.libra.home.charts

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thinkpad.libra.R
import com.example.thinkpad.libra.data.Order
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData

class ChartFragment:Fragment() {

    companion object {
        fun newInstance(info: String): ChartFragment {
            val args = Bundle()
            val fragment = ChartFragment()
            args.putString("info", info)
            fragment.arguments = args
            return fragment
        }
    }

    private var testOrderList = ArrayList<Order>()
    var entries = ArrayList<Entry>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View = inflater.inflate(R.layout.charts_fragment, container, false)

        addTestOrders(testOrderList)
        addOrdersIntoEntries(testOrderList, entries)

        var lineChart = root.findViewById<LineChart>(R.id.line_chart)

        val dataSet = LineDataSet(entries, "Label")
        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()

        return root
    }

    private fun addTestOrders(testOrderList: ArrayList<Order>) {
        for (i in 0 until 10) {
            testOrderList.add(Order((10*i.toDouble()).toString(), i.toString()))
        }
    }

    private fun addOrdersIntoEntries(testOrderList: ArrayList<Order>,entries:ArrayList<Entry>) {

        for (order in testOrderList) {
            entries.add(Entry(order.orderId.toFloat(), order.totalPrice.toFloat()))
        }

    }

}