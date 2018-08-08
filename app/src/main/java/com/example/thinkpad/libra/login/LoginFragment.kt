package com.example.thinkpad.libra.login

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.thinkpad.libra.R

class LoginFragment : Fragment() {
    private lateinit var phoneEdit: EditText
    private lateinit var authenCodeEdit: EditText
    private lateinit var loginButton: Button


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.login_fragment, container, false)
        with(root) {
            phoneEdit = findViewById(R.id.phone_edit)
            authenCodeEdit = findViewById(R.id.authen_code_exit)
            loginButton = findViewById(R.id.login_button)
        }

        loginButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (checkPhoneNotNull() && checkAuthenCodeNotNull()) {
                    sendGetAuthenCodeRequst()
                }
            }

        })

        return root
    }

    fun checkPhoneNotNull(): Boolean {
        var phoneInput = phoneEdit.text
        return (phoneInput==null)
    }

    fun checkAuthenCodeNotNull(): Boolean {
        var authenCodeInput = authenCodeEdit.text
        return (authenCodeInput == null)
    }

    fun sendGetAuthenCodeRequst() {
        TODO("发出请求")
    }
}