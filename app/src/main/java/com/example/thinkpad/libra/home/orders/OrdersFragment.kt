package com.example.thinkpad.libra.home.orders

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import com.example.thinkpad.libra.data.source.OrdersLab
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.card_item_view_order.view.*
import kotlinx.android.synthetic.main.orders_fragment.*

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

    private var testOrderList = ArrayList<Order>()
    private var authToken: String = ""
    private var mPreference: SharedPreferences? = null
    private var ordersAdapter: OrderAdapter? = null
    private var ordersLab: OrdersLab? = null
    private var mContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.orders_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initRefreshLayout()
        initToken()
        ordersLab = OrdersLab.getOrdersLab(authToken)
        addTestOrders(testOrderList)
        updateUI()
    }

    private fun initRefreshLayout() {
        smart_refresh_layout.setOnRefreshListener { refreshLayout ->
            updateUI()
            refreshLayout.finishRefresh(2000)
        }
        smart_refresh_layout.setOnLoadMoreListener { refreshLayout ->
            updateUI()
            refreshLayout.finishLoadMore(2000)
        }
    }

    private fun initRecyclerView() {
        orders_recycler_view.layoutManager = LinearLayoutManager(mContext)
        orders_recycler_view.adapter = ordersAdapter
    }

    private fun updateOrders() {
        if (testOrderList.size == 0) {
            testOrderList = ordersLab?.getOrderList() ?: ArrayList()
        }
    }

    private fun updateUI() {
        if (ordersAdapter == null) {
            updateOrders()
            ordersAdapter = OrderAdapter(testOrderList)
            orders_recycler_view.adapter = ordersAdapter
        } else {
            ordersAdapter?.notifyDataSetChanged()
        }
    }

    private fun initToken() {
        mPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val token = mPreference?.getString("token", "880611")
        authToken = token.toString()
    }

    private fun addTestOrders(testOrderList: ArrayList<Order>) {
        for (i in 0 until 3) {
            testOrderList.add(Order())
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
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
            itemView.text_time_content.text = mOrder.payTime
            itemView.text_date_content.text = mOrder.payTime
            itemView.text_value_content.text = "$" + mOrder.totalPrice
            itemView.text_order_id_content.text = mOrder.orderId

            itemView.delete_order_button_content.setOnClickListener {
                showSweetAlertDialog()
            }
        }

        override fun onClick(v: View?) {
            wantedOrderDetailList = ordersLab?.getWantedOrder(mOrder.orderId) ?: ArrayList()
        }

        private fun showSweetAlertDialog() {
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

                        this@OrdersFragment.ordersLab?.deleteOrder(mOrder.orderId)
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