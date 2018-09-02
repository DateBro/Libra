package com.example.thinkpad.libra.home.store

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thinkpad.libra.R
import com.example.thinkpad.libra.login.LoginActivity
import kotlinx.android.synthetic.main.store_fragment.*

class StoreFragment: Fragment() {

    companion object {
        fun newInstance(info: String): StoreFragment {
            val args = Bundle()
            val fragment = StoreFragment()
            args.putString("info", info)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.store_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exit_text.setOnClickListener { exitHome() }
    }

    private fun exitHome() {
        clearSharedPreference()
        val intent = activity?.let { LoginActivity.newIntent(it) }
        intent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun clearSharedPreference() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}