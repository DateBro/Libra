package com.example.thinkpad.libra.home.products

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import kotlinx.android.synthetic.main.item_view_product.view.*
import kotlinx.android.synthetic.main.products_fragment.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ProductsFragment: Fragment() {
    companion object {
        fun newInstance(info: String): ProductsFragment {
            val args = Bundle()
            val fragment = ProductsFragment()
            args.putString("info", info)
            fragment.arguments = args
            return fragment
        }
    }

    private val mHandler = Handler(Looper.getMainLooper())

    private val getAllProductsURL = "http://39.106.55.9:8088/goods/list"
    private var testProductList = ArrayList<Product>()
    private var authToken: String = ""
    private var mPreference: SharedPreferences? = null
    private var productsAdapter:ProductAdapter? = null
    private var mContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.products_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initRefreshLayout()
        initToken()
        sendGetAllProductsRequest()
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
        products_recycler_view.layoutManager = LinearLayoutManager(this.activity)
        products_recycler_view.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    private fun updateUI() {
        assureProductList(testProductList)
        try {
            productsAdapter = ProductAdapter(testProductList)
            products_recycler_view.adapter = productsAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun initToken() {
        mPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val token = mPreference?.getString("token", "880611")
        authToken = token.toString()
    }

    override fun onStart() {
        super.onStart()
        initToken()
    }

    override fun onResume() {
        super.onResume()
        initToken()
        assureProductList(testProductList)
    }

    private fun assureProductList(productList: ArrayList<Product>) {
        if (productList.size == 0) {
            productList.clear()
            productList.add(Product("苹果", "6.98", 1))
            productList.add(Product("香蕉", "3.58", 2))
            productList.add(Product("葡萄", "9.9", 3))
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
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

                mHandler.post { updateUI() }
            }
        })
    }

    private fun parseGetAllProductsJson(jsonData: String) {
        Log.e("parseAllProductsJson", jsonData)
        try {
            testProductList.clear()
            val jsonObjectTest = JSONObject(jsonData)
            val data = jsonObjectTest.getJSONArray("data")
            for (i in 0 until data.length()) {
                val jsonObject = data.getJSONObject(i)
                val id = jsonObject.getString("id").toInt()
                val name = jsonObject.getString("name")
                val price = jsonObject.getString("price")
                val product = Product(name, price, id)
                testProductList.add(product)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private inner class ProductHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_view_product, parent, false)), View.OnClickListener {

        var mProduct: Product = Product("Test", "$0.00")

        init {
            itemView.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            mProduct = product
            itemView.item_view_product_name.text = mProduct.productName
            itemView.item_view_product_value.text = "$" + mProduct.productValue
            itemView.item_view_product_id.text = mProduct.productId.toString()
        }

        override fun onClick(v: View?) {

        }
    }

    private inner class ProductAdapter(internal var productList: List<Product>) : RecyclerView.Adapter<ProductHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
            val inflater = LayoutInflater.from(mContext)
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