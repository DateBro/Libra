package com.example.thinkpad.libra.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.thinkpad.libra.R
import com.example.thinkpad.libra.home.HomeActivity
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : Fragment() {

    private val authCodeURL = "http://39.106.55.9:8088/account/authSms"
    private val loginURL = "http://39.106.55.9:8088/account/login"
    private val registerURL = "http://39.106.55.9:8088/account/register"
    private val phoneNumberRegex = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$"
    private val authCodeRegex = "[0-9]{4}"
    private val defaultMerchantName = "我的商铺"

    private var mPreference: SharedPreferences? = null

    override fun onStart() {
        super.onStart()
        checkSharedPref()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login_button.setOnClickListener {
            if (checkPhoneOk() && checkAuthCodeOk()) {
                val phoneNum = phone_edit.text.toString()
                val authCode = auth_code_edit.text.toString()
                sendLoginRequest(phoneNum, authCode)
            }
        }

        get_authen_code_button.setOnClickListener {
            if (checkPhoneOk()) {
                sendGetAuthCodeRequest()
            }
        }
    }

    private fun checkIsRegister(): Boolean {

        return false
    }

    private fun checkPhoneOk(): Boolean {
        return (checkPhoneNotNull() && checkPhoneNumRight())
    }

    private fun checkPhoneNotNull(): Boolean {
        val phoneInput = phone_edit.text
        if (phoneInput == null) {
            try {
                activity!!.runOnUiThread { Toast.makeText(context, "请输入手机号码", Toast.LENGTH_SHORT).show() }
            } catch (e: Exception) {
                Log.e("uiException", e.message)
            }
        }
        return (phoneInput != null)
    }

    private fun checkPhoneNumRight(): Boolean {
        val phoneInput = phone_edit.text
        return phoneInput.matches(Regex(phoneNumberRegex))
    }

    private fun checkAuthCodeOk(): Boolean {
        return (checkAuthCodeNotNull() && checkAuthCodeRight())
    }

    private fun checkAuthCodeRight(): Boolean {
        val authCodeInput = auth_code_edit.text
        return authCodeInput.matches(Regex(authCodeRegex))
    }

    private fun checkAuthCodeNotNull(): Boolean {
        val authCodeInput = auth_code_edit.text
        if (authCodeInput == null) {
            Toast.makeText(activity, "请输入验证码", Toast.LENGTH_SHORT).show()
        }
        return (authCodeInput != null)
    }

    private fun sendGetAuthCodeRequest() {
        val client = OkHttpClient()
        val request = createGetAuthCodeRequest()
        getAuthCodeCall(request, client)
    }

    private fun createGetAuthCodeRequest(): Request {
        val phoneNum = phone_edit.text.toString()
        val json = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(json, createGetAuthCodeJson(phoneNum))

        return Request.Builder()
                .url(authCodeURL)
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

    private fun sendLoginRequest(phoneNum: String, authCode: String) {
        val client = OkHttpClient()
        val request = createLoginRequest(phoneNum, authCode)
        loginCall(request, client)
    }

    private fun createLoginRequest(phoneNum: String, authCode: String): Request {
        val json = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(json, createLoginJson(phoneNum, authCode))

        return Request.Builder()
                .url(loginURL)
                .post(body)
                .build()
    }

    private fun createLoginJson(phoneNum: String, authCode: String): String {
        val jsonLogin = JSONObject()
        jsonLogin.put("telphone", phoneNum)
        jsonLogin.put("code", authCode)

        return jsonLogin.toString()
    }

    private fun loginCall(request: Request, client: OkHttpClient) {
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                responseBody?.let { parseLoginJson(it) }
            }
        })
    }

    private fun parseLoginJson(jsonData: String) {
        try {
            val jsonObjectTest = JSONObject(jsonData)
            val data = jsonObjectTest.getString("data")
            val tokenObject = JSONObject(data)
            val token: String = tokenObject.getString("token")
            Log.e("whereIsToken", token)
            context?.let { setSharedPreference(it, token) }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setSharedPreference(context: Context, token: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.putBoolean("autoSignIn", true)
        editor.apply()

        loginHome()
    }

    private fun checkSharedPref() {
        mPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val autoSignIn = mPreference?.getBoolean("autoSignIn", false)
        if (autoSignIn!!) {
            loginHome()
        }
    }

    private fun loginHome() {
        val intent = activity?.let { HomeActivity.newIntent(it) }
        intent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}