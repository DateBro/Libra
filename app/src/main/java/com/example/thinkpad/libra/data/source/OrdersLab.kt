package com.example.thinkpad.libra.data.source

import android.annotation.SuppressLint
import android.util.Log
import com.example.thinkpad.libra.data.Order
import com.example.thinkpad.libra.data.OrderDetail
import com.example.thinkpad.libra.data.Product
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * @author  Zhiyong Zhao
 */

class OrdersLab private constructor(token: String) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var ordersLab: OrdersLab? = null

        @Synchronized
        fun getOrdersLab(token: String): OrdersLab? {
            if (ordersLab == null) {
                ordersLab = OrdersLab(token)
            }
            return ordersLab
        }
    }

    private val getAllOrdersURL = "http://39.106.55.9:8088/order/list"
    private val deleteOrderURL = "http://39.106.55.9:8088/order/"
    private val getOrderURL = "http://39.106.55.9:8088/order/"

    private var mOrdersList: ArrayList<Order> = ArrayList()
    private var authToken: String = token
    private var wantedOrder: Order? = null
    private var wantedOrderDetailList: ArrayList<OrderDetail> = ArrayList()

    init {
        @Synchronized
        if (mOrdersList.size == 0) {
            updateOrdersList()
        }
    }

    @Synchronized
    fun getOrderList(): ArrayList<Order> {
        updateOrdersList()
        return mOrdersList
    }

    @Synchronized
    fun deleteOrder(orderId: String) {
        sendDeleteOrderRequest(orderId)
        updateOrdersList()
    }

    @Synchronized
    fun getWantedOrder(orderId: String): ArrayList<OrderDetail> {
        sendGetWantedOrderRequest(orderId)
        return wantedOrderDetailList
    }

    private fun updateOrdersList() {
        if (mOrdersList.size == 0) {
            sendGetAllOrdersRequest()
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
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                parseGetAllOrdersJson(responseBody.toString())
            }
        })
    }

    private fun parseGetAllOrdersJson(jsonData: String) {
        try {
            mOrdersList = ArrayList()
            val dataJsonObject = JSONObject(jsonData)
            val data = dataJsonObject.getJSONArray("data")
            for (i in 0 until data.length()) {
                val orderJsonObject = data.getJSONObject(i)
                val orderId:String = orderJsonObject.get("id").toString()
                val payTime: String = orderJsonObject.get("payTime").toString()
                val totalPrice: String = orderJsonObject.get("totalPrice").toString()
                val order = Order(totalPrice, orderId, payTime)
                mOrdersList.add(order)
            }
        } catch (e: JSONException) {
            Log.e("parseAllOrderExc", e.message)
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
            Log.e("parseWantedOrderExc", e.message)
            e.printStackTrace()
        }
    }
}
