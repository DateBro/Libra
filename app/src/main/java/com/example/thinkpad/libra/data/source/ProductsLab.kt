package com.example.thinkpad.libra.data.source

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.thinkpad.libra.data.Product
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ProductsLab private constructor(token: String) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var productsLab: ProductsLab? = null

        @Synchronized
        fun getProductsLab(token: String): ProductsLab? {
            if (productsLab == null) {
                productsLab = ProductsLab(token)
            }
            return productsLab
        }
    }

    private val getAllProductsURL = "http://39.106.55.9:8088/goods/list"
    private var mProductsList:ArrayList<Product> = ArrayList()
    private var authToken: String = token

    init {
        @Synchronized
        if (mProductsList.size == 0) {
            Log.e("currentCallTime", "initList" + System.currentTimeMillis())
            initProductsList()
        }
    }

    @Synchronized
    fun getProductList(): ArrayList<Product> {
        initProductsList()
        return mProductsList
    }

    private fun initProductsList() {
        if (mProductsList.size == 0) {
            sendGetAllProductsRequest()
        }
    }

    private fun sendGetAllProductsRequest() {
        val client = OkHttpClient()
        val request = createGetAllProductsRequest()
        getAllProductsCall(request, client)
    }

    private fun createGetAllProductsRequest(): Request {
        return Request.Builder()
                .url(getAllProductsURL)
                .header("Authorization", authToken)
                .get()
                .build()
    }

    private fun getAllProductsCall(request: Request, client: OkHttpClient) {
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                parseGetAllProductsJson(responseBody.toString())
                Log.e("productLabNull", responseBody)
            }
        })
    }

    private fun parseGetAllProductsJson(jsonData: String) {
        try {
            mProductsList = ArrayList()
            val jsonObjectTest = JSONObject(jsonData)
            val data = jsonObjectTest.getJSONArray("data")
            for (i in 0 until data.length()) {
                val jsonObject = data.getJSONObject(i)
                val id = jsonObject.getString("id").toInt()
                val name = jsonObject.getString("name")
                val price = jsonObject.getString("price")
                val product = Product(name, price, id)
                mProductsList.add(product)
            }
            Log.e("caughtJsonException", "complete list" + System.currentTimeMillis())
            Log.e("caughtJsonException", "Lab" + mProductsList.size)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}