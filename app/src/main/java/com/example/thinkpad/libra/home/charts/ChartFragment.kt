package com.example.thinkpad.libra.home.charts

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thinkpad.libra.R

class ChartFragment:Fragment() {

    companion object {
        fun newInstance(info: String): ChartFragment {
            val args = Bundle()
            val fragment = ChartFragment()
            args.putString("info", info)
            fragment.setArguments(args)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.charts_fragment, container,false)
    }
}