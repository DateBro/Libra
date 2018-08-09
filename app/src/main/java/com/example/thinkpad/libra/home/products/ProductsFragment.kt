package com.example.thinkpad.libra.home.products

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thinkpad.libra.R

class ProductsFragment: Fragment() {

    companion object {
        fun newInstance(info: String): ProductsFragment {
            val args = Bundle()
            val fragment = ProductsFragment()
            args.putString("info", info)
            fragment.setArguments(args)
            return fragment
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.products_fragment, container,false)
    }
}