package com.example.thinkpad.libra.home.orders

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thinkpad.libra.R
import com.example.thinkpad.libra.data.Order
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.card_item_view_order.*
import kotlinx.android.synthetic.main.orders_fragment.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class OrdersFragment: Fragment() {

    private val getAllOrdersURL = "http://39.106.55.9:8088/order/list"
    private val deleteOrderURL = "http://39.106.55.9:8088/order/{id}"
    private val getOrderURL = "http://39.106.55.9:8088/order/{id}"

    companion object {
        fun newInstance(info: String): OrdersFragment {
            val args = Bundle()
            val fragment = OrdersFragment()
            args.putString("info", info)
            fragment.arguments = args
            return fragment
        }
    }

    private var testOrderList = ArrayList<Order>()

    private var authToken: String = ""
    private var mPreference: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View = inflater.inflate(R.layout.orders_fragment, container, false)

        initToken()
        addTestOrders(testOrderList)
        initOrders()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orders_recycler_view.layoutManager = LinearLayoutManager(activity)
        orders_recycler_view.adapter = OrderAdapter(testOrderList)
        orders_recycler_view.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        smart_refresh_layout.setOnRefreshListener { refreshLayout ->
            initOrders()
            refreshLayout.finishRefresh(2000)
        }

        smart_refresh_layout.setOnLoadMoreListener{refreshLayout ->
            initOrders()
            refreshLayout.finishLoadMore(2000)
        }
    }

    private fun initOrders() {
        sendGetAllOrdersRequest()
        //在这里应该把获得的订单加入列表，但现在没有测试数据没法弄
    }

    private fun initToken() {
        mPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val token = mPreference?.getString("token", "880611")
        authToken = token.toString()
    }

    private fun addTestOrders(testOrderList: ArrayList<Order>) {
        for (i in 0 until 10) {
            testOrderList.add(Order(100.0))
        }
    }

    private fun sendGetAllOrdersRequest() {
        val client = OkHttpClient()
        val request = createGetAllOrdersRequest()
        getAllOrdersCall(request, client)
    }

    private fun createGetAllOrdersRequest(): Request {
        return Request.Builder()
                .url(getAllOrdersURL)
                .header("Authorization", authToken)
                .get()
                .build()
    }

    private fun getAllOrdersCall(request: Request, client: OkHttpClient) {
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                Log.e("ResponseSuccess", responseBody)
            }
        })
    }

    private inner class OrderHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.card_item_view_order, parent, false)), View.OnClickListener {

        var order: Order = Order(10.0)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(order: Order) {
            this.order = order
            text_time_content?.text = order.productName
            text_date_content?.text = order.orderValue.toString()
        }

        override fun onClick(v: View?) {

        }
    }

    private inner class OrderAdapter(internal var orderList: List<Order>) : RecyclerView.Adapter<OrderHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
            val inflater = LayoutInflater.from(activity)
            return OrderHolder(inflater, parent)
        }

        override fun onBindViewHolder(holder: OrderHolder, position: Int) {
            val order = orderList[position]
            holder.bind(order)
        }

        override fun getItemCount(): Int {
            return orderList.size
        }
    }
}