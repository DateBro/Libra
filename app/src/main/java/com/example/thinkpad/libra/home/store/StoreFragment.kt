package com.example.thinkpad.libra.home.store

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thinkpad.libra.R

class StoreFragment: Fragment() {

    companion object {
        fun newInstance(info: String): StoreFragment {
            val args = Bundle()
            val fragment = StoreFragment()
            args.putString("info", info)
            fragment.setArguments(args)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.store_fragment, container,false)
    }
}