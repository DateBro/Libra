package com.example.thinkpad.libra.login

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.thinkpad.libra.R
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginFragment : Fragment() {

    private val ApiURL = "http://39.106.55.9:8088/account/authSms"
    private val testPhoneNum = "15665827713"

    private lateinit var phoneEdit: EditText
    private lateinit var authCodeEdit: EditText
    private lateinit var loginButton: Button
    private lateinit var getAuthCodeButton: Button


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.login_fragment, container, false)
        with(root) {
            phoneEdit = findViewById(R.id.phone_edit)
            authCodeEdit = findViewById(R.id.authen_code_exit)
            loginButton = findViewById(R.id.login_button)
            getAuthCodeButton = findViewById(R.id.get_authen_code_button)
        }

        loginButton.setOnClickListener {
            if (checkPhoneNotNull() && checkAuthCodeNotNull()) {
                sendGetAuthCodeRequest()
            }
        }

        getAuthCodeButton.setOnClickListener { sendGetAuthCodeRequest() }

        return root
    }

    private fun checkPhoneNotNull(): Boolean {
        val phoneInput = phoneEdit.text
        return (phoneInput==null)
    }

    private fun checkAuthCodeNotNull(): Boolean {
        val authCodeInput = authCodeEdit.text
        return (authCodeInput == null)
    }

    private fun sendGetAuthCodeRequest() {
        val client = OkHttpClient()
        val request = createGetAuthCodeRequest()
        getAuthCodeCall(request, client)
    }

    private fun createGetAuthCodeRequest(): Request {
        val json = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(json, createGetAuthCodeJson(testPhoneNum))

        return Request.Builder()
                .url(ApiURL)
                .post(body)
                .build()
    }

    private fun getAuthCodeCall(request: Request, client: OkHttpClient) {
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("BooksLabFail", e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("ResponseSuccess", response.code().toString())
            }
        })
    }

    private fun createGetAuthCodeJson(phoneNum: String): String {
        val jsonGetAuthCode = JSONObject()
        jsonGetAuthCode.put("telphone", phoneNum)

        return jsonGetAuthCode.toString()
    }

}