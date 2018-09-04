package com.example.thinkpad.libra.home.products

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.thinkpad.libra.data.source.ProductsLab
import kotlinx.android.synthetic.main.item_view_product.*
import kotlinx.android.synthetic.main.item_view_product.view.*
import kotlinx.android.synthetic.main.products_fragment.*

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

    private var testProductList = ArrayList<Product>()
    private var authToken: String = ""
    private var mPreference: SharedPreferences? = null
    private var productsAdapter:ProductAdapter? = null
    private var productsLab: ProductsLab? = null
    private var mContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.products_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initToken()
        productsLab =ProductsLab.getProductsLab(authToken)
        updateUI()
    }

    private fun initRecyclerView() {
        products_recycler_view.layoutManager = LinearLayoutManager(this.activity)
        products_recycler_view.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    private fun updateUI() {
        if (productsAdapter == null) {
            makeSureProductListNotNull()
            productsAdapter = ProductAdapter(testProductList)
            products_recycler_view.adapter = productsAdapter
        }else {
            productsAdapter?.notifyDataSetChanged()
        }
        Log.e("currentCallTime", "lstSize" + testProductList.size + System.currentTimeMillis())
    }

    private fun makeSureProductListNotNull() {
        if (testProductList.size == 0) {
//            Log.e("currentCallTime", "getList" + System.currentTimeMillis())
//            testProductList = productsLab?.getProductList() ?: ArrayList()
//            if (testProductList.size == 0) {
//                Log.e("testListNull", "Null " + System.currentTimeMillis())
//            }
            for (i in 0 until 3) {
                testProductList.add(Product(id = i))
            }
        }
    }

    private fun initToken() {
        mPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val token = mPreference?.getString("token", "880611")
        authToken = token.toString()
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

    override fun onStart() {
        super.onStart()
        initToken()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

}