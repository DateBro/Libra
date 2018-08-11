package com.example.thinkpad.libra.home.orders

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thinkpad.libra.R
import com.example.thinkpad.libra.data.Order
import kotlinx.android.synthetic.main.cell_title_layout.*
import kotlinx.android.synthetic.main.card_item_view_order.*




class OrdersFragment: Fragment() {

    companion object {
        fun newInstance(info: String): OrdersFragment {
            val args = Bundle()
            val fragment = OrdersFragment()
            args.putString("info", info)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var orderListView: RecyclerView
    private var testOrderList = ArrayList<Order>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View = inflater.inflate(R.layout.orders_fragment, container, false)

        addTestOrders(testOrderList)

        card_folding_cell.setOnClickListener { card_folding_cell.toggle(false) }

        orderListView = root.findViewById(R.id.orders_recycler_view)
        orderListView.layoutManager = LinearLayoutManager(this.activity)
        orderListView.adapter = OrderAdapter(testOrderList)
        orderListView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        return root
    }

    private fun addTestOrders(testOrderList: ArrayList<Order>) {
        for (i in 0 until 10) {
            testOrderList.add(Order(100.0))
        }
    }

    private inner class OrderHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.card_item_view_order, parent, false)), View.OnClickListener {
        var order: Order = Order(10.0)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(order: Order) {
            this.order = order
            text_order_code_title.text = order.productName
            text_price_title.text = order.orderValue.toString()
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