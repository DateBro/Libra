package com.example.thinkpad.libra.home.orders

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thinkpad.libra.R
import com.example.thinkpad.libra.data.Order
import com.example.thinkpad.libra.data.OrderDetail
import com.example.thinkpad.libra.data.Product
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.card_item_view_order.view.*
import kotlinx.android.synthetic.main.orders_fragment.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import android.os.Looper

/**
 * @author  Zhiyong Zhao
 */

class OrdersFragment : Fragment() {
    companion object {
        fun newInstance(info: String): OrdersFragment {
            val args = Bundle()
            val fragment = OrdersFragment()
            args.putString("info", info)
            fragment.arguments = args
            return fragment
        }
    }

    private val mHandler = Handler(Looper.getMainLooper())
    private val getAllOrdersURL = "http://39.106.55.9:8088/order/list"
    private val deleteOrderURL = "http://39.106.55.9:8088/order/"
    private val getOrderURL = "http://39.106.55.9:8088/order/"
    private var wantedOrder: Order? = null
    private var wantedOrderDetailList: ArrayList<OrderDetail> = ArrayList()
    private var testOrderList = ArrayList<Order>()
    private var authToken: String = ""
    private var mPreference: SharedPreferences? = null
    private var ordersAdapter: OrderAdapter? = null
    private var mContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.orders_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initRefreshLayout()
        initToken()
        sendGetAllOrdersRequest()
    }

    private fun addTestOrders() {
        testOrderList.clear()
        testOrderList.add(Order())
    }

    private fun initRefreshLayout() {
        smart_refresh_layout.setOnRefreshListener { refreshLayout ->
            mHandler.post { updateUI() }
            refreshLayout.finishRefresh(2000)
        }
        smart_refresh_layout.setOnLoadMoreListener { refreshLayout ->
            mHandler.post { updateUI() }
            refreshLayout.finishLoadMore(2000)
        }
    }

    private fun initRecyclerView() {
        orders_recycler_view.layoutManager = LinearLayoutManager(mContext)
        orders_recycler_view.adapter = ordersAdapter
    }

    private fun updateUI() {
        if (ordersAdapter == null) {
            if (testOrderList.size == 0) {
                addTestOrders()
            }
            ordersAdapter = OrderAdapter(testOrderList)
            orders_recycler_view?.adapter = ordersAdapter
        } else {
            ordersAdapter?.notifyDataSetChanged()
        }
    }

    private fun initToken() {
        mPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val token = mPreference?.getString("token", "880611")
        authToken = token.toString()
    }

    override fun onResume() {
        super.onResume()
        initToken()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
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
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                Log.e("getAllOrdersCall", responseBody)
                parseGetAllOrdersJson(responseBody.toString())

                mHandler.post { updateUI() }
            }
        })
    }

    private fun parseGetAllOrdersJson(jsonData: String) {
        try {
            testOrderList.clear()
            val dataJsonObject = JSONObject(jsonData)
            val data = dataJsonObject.getJSONArray("data")
            for (i in 0 until data.length()) {
                val orderJsonObject = data.getJSONObject(i)
                val orderId:String = orderJsonObject.get("id").toString()
                val payTime: String = orderJsonObject.get("payTime").toString()
                val totalPrice: String = orderJsonObject.get("totalPrice").toString()
                val order = Order(totalPrice, orderId, payTime)
                testOrderList.add(order)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun sendDeleteOrderRequest(orderId: String) {
        val client = OkHttpClient()
        val request = createDeleteOrderRequest(orderId)
        deleteOrderCall(request, client)
    }

    private fun createDeleteOrderRequest(orderId: String): Request {
        return Request.Builder()
                .url(deleteOrderURL+orderId)
                .header("Authorization", authToken)
                .get()
                .build()
    }

    private fun deleteOrderCall(request: Request, client: OkHttpClient) {
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {}
        })
    }

    private fun sendGetWantedOrderRequest(orderId: String) {
        val client = OkHttpClient()
        val request = createGetWantedOrderRequest(orderId)
        getWantedOrderCall(request, client)
    }

    private fun createGetWantedOrderRequest(orderId: String): Request {
        return Request.Builder()
                .url(getOrderURL+orderId)
                .header("Authorization", authToken)
                .get()
                .build()
    }

    private fun getWantedOrderCall(request: Request, client: OkHttpClient) {
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                parseGetWantedOrderJson(response.body().toString())
            }
        })
    }

    private fun parseGetWantedOrderJson(jsonData: String) {
        try {
            wantedOrder = null
            val dataJsonObject = JSONObject(jsonData)
            val orderId:String = dataJsonObject.get("id").toString()
            val payTime: String = dataJsonObject.get("payTime").toString()
            val totalPrice: String = dataJsonObject.get("totalPrice").toString()
            wantedOrder = Order(totalPrice, orderId, payTime)

            val orderProducts = dataJsonObject.getJSONArray("orderGoods")
            for (i in 0 until orderProducts.length()) {
                val productsJsonObject = orderProducts.getJSONObject(i)
                val productJson = productsJsonObject.getJSONObject("goods")
                val productName = productJson.getString("name")
                val productPrice:String = productJson.get("price").toString()
                val weight: String = productsJsonObject.get("weight").toString()
                val totalPay: String = productsJsonObject.get("totalPay").toString()
                val orderProduct = Product(productName, productPrice)
                val orderDetail = OrderDetail(orderProduct, weight, totalPay)
                wantedOrderDetailList.add(orderDetail)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private inner class OrderHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.card_item_view_order, parent, false)), View.OnClickListener {

        var mOrder: Order = Order()
        var wantedOrderDetailList: ArrayList<OrderDetail> = ArrayList()

        init {
            itemView.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bind(order: Order) {
            mOrder = order
            if (mOrder.payTime.length > 9) {
                itemView.text_time_content.text = mOrder.payTime.subSequence(12, 16)
                itemView.text_date_content.text = mOrder.payTime.subSequence(0, 10)
            } else {
                itemView.text_time_content.text = mOrder.payTime
                itemView.text_date_content.text = mOrder.payTime
            }
            itemView.text_value_content.text = "$" + mOrder.totalPrice
            itemView.text_order_id_content.text = mOrder.orderId

            itemView.delete_order_button_content.setOnClickListener {
                mHandler.post { showSweetAlertDialog(mOrder.orderId)
                    itemView.invalidate()
                }
            }
        }

        override fun onClick(v: View?) {
            wantedOrderDetailList
        }

        private fun showSweetAlertDialog(orderId: String) {
            SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("确认删除？")
                    .setContentText("删除的订单将不能恢复！")
                    .setConfirmText("是的，删除！")
                    .setConfirmClickListener { sDialog ->
                        sDialog
                                .setTitleText("已删除！")
                                .setContentText("已删除订单！")
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)

                        testOrderList.remove(mOrder)
                        sendDeleteOrderRequest(orderId)
                        smart_refresh_layout.autoRefresh()
                    }
                    .show()
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