package com.example.thinkpad.libra.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.thinkpad.libra.R

class LoginActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context):Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login_activity)

        val fm = supportFragmentManager
        var loginFragment = fm.findFragmentById(R.id.login_fragment_container)
        if (loginFragment == null) {
            loginFragment = LoginFragment()
            fm.beginTransaction().add(R.id.login_fragment_container, loginFragment).commit()
        }
    }
}