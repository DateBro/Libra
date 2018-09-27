package com.example.thinkpad.libra.home.charts

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thinkpad.libra.R
import com.example.thinkpad.libra.data.Order
import com.example.thinkpad.libra.data.Product
import com.example.thinkpad.libra.utility.XAxisValueFormatter
import com.example.thinkpad.libra.utility.YAxisValueFormatter
import com.example.thinkpad.libra.utility.PriceValueFormatter
import com.github.mikephil.charting.data.*
import kotlinx.android.synthetic.main.charts_fragment.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

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

    private val mHandler = Handler(Looper.getMainLooper())

    private val getPopularProductURL = "http://39.106.55.9:8088/analysis/goods"
    private val getDailyOrderURL = "http://39.106.55.9:8088/analysis/orders"
    private var authToken: String = ""
    private var mPreference: SharedPreferences? = null
    private var dailyOrderList = ArrayList<Order>()
    private var popularProductList = ArrayList<Product>()
    private var lineEntries:ArrayList<Entry> = ArrayList()
    private var barEntries: ArrayList<BarEntry> = ArrayList()
    private var barXAxisStringValue: Array<String> = Array(50) { "无" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.charts_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToken()
        sendGetDailyOrdersRequest()
        sendGetPopularProductsRequest()
    }

    private fun addOrdersIntoEntries(testOrderList: ArrayList<Order>,entries:ArrayList<Entry>) {
        if (testOrderList.size > 0) {
            entries.clear()
            for (i in 0 until testOrderList.size) {
                entries.add(Entry(i.toFloat(), testOrderList[i].totalPrice.toFloat()))
            }
        }
    }

    private fun addProductsIntoEntries(popularProductList: ArrayList<Product>, barEntries: ArrayList<BarEntry>) {
        if (popularProductList.size > 0) {
            barEntries.clear()
            for (i in 0 until popularProductList.size) {
                barEntries.add(BarEntry(i.toFloat(), popularProductList[i].productValue.toFloat()))
            }
        }
    }

    private fun configureLineChart() {
        addOrdersIntoEntries(dailyOrderList, lineEntries)
        configureLineAxis()

        val dataSet = LineDataSet(lineEntries, "一周内营业额变化图")
        val lineData = LineData(dataSet)
        lineData.setValueFormatter(PriceValueFormatter())
        line_chart.data = lineData
        line_chart.invalidate()
    }

    private fun configureBarChart() {
        addProductsIntoEntries(popularProductList, barEntries)
        configureBarAxis()

        val barDataSet = BarDataSet(barEntries, "最近商品购买量排行")
        val barData = BarData(barDataSet)
        barData.barWidth = 0.5f
        barDataSet.valueFormatter = PriceValueFormatter()
        bar_chart.data = barData
        bar_chart.setFitBars(true)
        bar_chart.invalidate()
    }

    private fun configureBarAxis() {
        val barYAxis = bar_chart.axisLeft
        barYAxis.valueFormatter = YAxisValueFormatter()

        val barXAxis = bar_chart.xAxis
        barXAxis.granularity = 1f
        for (i in 0 until popularProductList.size) {
            barXAxisStringValue[i] = popularProductList[i].productName
        }
        barXAxis.valueFormatter = XAxisValueFormatter(barXAxisStringValue)
    }

    private fun configureLineAxis() {
        val lineYAxis = line_chart.axisLeft
        lineYAxis.valueFormatter = YAxisValueFormatter()
    }

    private fun initToken() {
        mPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val token = mPreference?.getString("token", "19880611")
        authToken = token.toString()
    }

    private fun sendGetPopularProductsRequest() {
        val client = OkHttpClient()
        val request = createGetPopularProductsRequest()
        getPopularProductsCall(request, client)
    }

    private fun createGetPopularProductsRequest(): Request {
        return Request.Builder()
                .url(getPopularProductURL)
                .header("Authorization", authToken)
                .get()
                .build()
    }

    private fun getPopularProductsCall(request: Request, client: OkHttpClient) {
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                parseGetPopularProductsJson(responseBody.toString())

                mHandler.post { configureBarChart() }
            }
        })
    }

    private fun parseGetPopularProductsJson(jsonData: String) {
        try {
            popularProductList.clear()
            val dataJsonObject = JSONObject(jsonData)
            val data = dataJsonObject.getJSONArray("data")
            for (i in 0 until data.length()) {
                val productJsonObject = data.getJSONObject(i)
                val productMoney: String = productJsonObject.get("money").toString()
                val productName: String = productJsonObject.get("name").toString()
                val popularProduct = Product(productName, productMoney)
                popularProductList.add(popularProduct)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun sendGetDailyOrdersRequest() {
        val client = OkHttpClient()
        val request = createGetDailyOrdersRequest()
        getDailyOrdersCall(request, client)
    }

    private fun createGetDailyOrdersRequest(): Request {
        return Request.Builder()
                .url(getDailyOrderURL)
                .header("Authorization", authToken)
                .get()
                .build()
    }

    private fun getDailyOrdersCall(request: Request, client: OkHttpClient) {
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                parseGetDailyOrdersJson(responseBody.toString())

                mHandler.post { configureLineChart() }
            }
        })
    }

    private fun parseGetDailyOrdersJson(jsonData: String) {
        try {
            dailyOrderList.clear()
            val dataJsonObject = JSONObject(jsonData)
            val data = dataJsonObject.getJSONArray("data")
            for (i in 0 until 7) {
                val orderJsonObject = data.getJSONObject(i)
                val orderMoney: String = orderJsonObject.get("money").toString()
                val orderDate: String = orderJsonObject.get("date").toString()
                dailyOrderList.add(Order(orderMoney, "0", orderDate))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}