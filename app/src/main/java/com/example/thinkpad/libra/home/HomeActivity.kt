package com.example.thinkpad.libra.home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import com.example.thinkpad.libra.R
import com.example.thinkpad.libra.home.charts.ChartFragment
import com.example.thinkpad.libra.home.orders.OrdersFragment
import com.example.thinkpad.libra.home.products.ProductsFragment
import com.example.thinkpad.libra.home.store.StoreFragment
import com.example.thinkpad.libra.utility.BottomNavigationViewHelper

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager:ViewPager
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initView()
    }

    private fun initView() {
        initViewPager()

        initBottomNavigationView()
    }

    private fun initViewPager() {
        viewPager = findViewById(R.id.viewpager)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        setupViewPager(viewPager)
    }

    private fun initBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        //默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_products -> viewPager.currentItem = 0
                R.id.item_orders -> viewPager.currentItem = 1
                R.id.item_charts -> viewPager.currentItem = 2
                R.id.item_profile -> viewPager.currentItem = 3
            }
            false
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(this.supportFragmentManager)

        adapter.addFragment(ProductsFragment.newInstance("商品"))
        adapter.addFragment(OrdersFragment.newInstance("订单"))
        adapter.addFragment(ChartFragment.newInstance("报表"))
        adapter.addFragment(StoreFragment.newInstance("我的商铺"))
        viewPager.adapter = adapter
    }
}
