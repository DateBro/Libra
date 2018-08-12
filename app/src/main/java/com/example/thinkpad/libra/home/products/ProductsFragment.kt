package com.example.thinkpad.libra.home.products

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thinkpad.libra.R
import com.example.thinkpad.libra.data.Product
import kotlinx.android.synthetic.main.item_view_product.*
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

    private lateinit var productListView: RecyclerView
    private var testProductList = ArrayList<Product>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root: View = inflater.inflate(R.layout.products_fragment, container, false)

        addTestProducts(testProductList)
        initRecyclerView(root)

        add_product_image.setOnClickListener { TODO("添加新的商品") }

        return root
    }

    private fun initRecyclerView(root: View) {
        this.productListView = root.findViewById(R.id.products_recycler_view)
        this.productListView.layoutManager = LinearLayoutManager(this.activity)
        this.productListView.adapter = ProductAdapter(testProductList)
        this.productListView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    private fun addTestProducts(testProductList: ArrayList<Product>) {
        for (i in 0 until 10) {
            testProductList.add(Product("Banana", 10.5))
        }
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
            item_view_delete_image.setOnClickListener(View.OnClickListener {
                TODO("删除后弹出对话框？")
            })
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