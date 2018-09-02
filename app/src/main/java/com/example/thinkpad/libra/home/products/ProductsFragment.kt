package com.example.thinkpad.libra.home.products

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
import com.example.thinkpad.libra.data.Product
import kotlinx.android.synthetic.main.item_view_product.*
import kotlinx.android.synthetic.main.products_fragment.*
import okhttp3.*
import java.io.IOException


class ProductsFragment: Fragment() {

    private val getAllProductsURL = "http://39.106.55.9:8088/goods/list"

    companion object {
        fun newInstance(info: String): ProductsFragment {
            val args = Bundle()
            val fragment = ProductsFragment()
            args.putString("info", info)
            fragment.arguments = args
            return fragment
        }
    }

    private var testProductList = ArrayList<Product>()

    private var authToken: String = ""
    private var mPreference: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View = inflater.inflate(R.layout.products_fragment, container, false)

        initProducts()
        addTestProducts(testProductList)

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToken()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        products_recycler_view.layoutManager = LinearLayoutManager(this.activity)
        products_recycler_view.adapter = ProductAdapter(testProductList)
        products_recycler_view.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

    }

    private fun addTestProducts(testProductList: ArrayList<Product>) {
        for (i in 0 until 10) {
            testProductList.add(Product("Banana", 10.5))
        }
    }

    private fun initProducts() {
        sendGetAllProductsRequest()
        //在这里应该把获得的产品信息加入列表，但现在没有测试数据没法弄
    }

    private fun initToken() {
        mPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val token = mPreference?.getString("token", "880611")
        authToken = token.toString()
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
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                Log.e("ResponseSuccessProduct", responseBody)
            }
        })
    }

    private inner class ProductHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_view_product, parent, false)), View.OnClickListener {

        var product: Product = Product("Pineapple")

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(product: Product) {
            this.product = product
            item_view_product_name?.text = product.productName
            item_view_product_value?.text = product.productValue.toString()

            //还缺少一个监听器
            item_view_delete_image?.setOnClickListener {
                TODO("删除后弹出对话框？")
            }
        }

        override fun onClick(v: View?) {

        }
    }

    private inner class ProductAdapter(internal var productList: List<Product>) : RecyclerView.Adapter<ProductHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
            val inflater = LayoutInflater.from(activity)
            return ProductHolder(inflater, parent)
        }

        override fun onBindViewHolder(holder: ProductHolder, position: Int) {
            val product = productList[position]
            holder.bind(product)
        }

        override fun getItemCount(): Int {
            return productList.size
        }
    }

}