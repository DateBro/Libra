package com.example.thinkpad.libra.home.store

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thinkpad.libra.R
import com.example.thinkpad.libra.data.Order
import com.example.thinkpad.libra.login.LoginActivity
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.store_fragment.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

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

    private val mHandler = Handler(Looper.getMainLooper())
    private val getTodayOrderURL = "http://39.106.55.9:8088/analysis/orders"
    private var authToken: String = ""
    private var todayOrder = Order()
    private var mPreference: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.store_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initToken()

        sendGetTodayOrdersRequest()
    }

    private fun initView() {
        exit_linearLayout.setOnClickListener { exitHome() }
        about_linearLayout.setOnClickListener { showAboutUsInfoDialog() }
        setting_linearLayout.setOnClickListener { showSettingInfoDialog() }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        text_today_income.text = "$" + todayOrder.totalPrice
        text_today_order_num.text = todayOrder.orderId
    }

    private fun showAboutUsInfoDialog() {
        SweetAlertDialog(activity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("关于我们")
                .setContentText("SDU OUT 工作室齐鲁软件大赛参赛作品")
                .setConfirmText("OK")
                .setCustomImage(R.drawable.sdu)
                .setConfirmClickListener(null)
                .show()
    }

    private fun showSettingInfoDialog() {
        SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("设置")
                .setContentText("暂时没有设置相关选项，敬请谅解。")
                .setConfirmText("OK")
                .setConfirmClickListener(null)
                .show()
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

    private fun initToken() {
        mPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val token = mPreference?.getString("token", "19880611")
        authToken = token.toString()
    }

    private fun sendGetTodayOrdersRequest() {
        val client = OkHttpClient()
        val request = createGetTodayOrdersRequest()
        getTodayOrdersCall(request, client)
    }

    private fun createGetTodayOrdersRequest(): Request {
        return Request.Builder()
                .url(getTodayOrderURL)
                .header("Authorization", authToken)
                .get()
                .build()
    }

    private fun getTodayOrdersCall(request: Request, client: OkHttpClient) {
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()
                parseGetTodayOrdersJson(responseBody.toString())

                mHandler.post { updateUI() }
            }
        })
    }

    private fun parseGetTodayOrdersJson(jsonData: String) {
        try {
            val dataJsonObject = JSONObject(jsonData)
            val data = dataJsonObject.getJSONArray("data")
            val orderJsonObject = data.getJSONObject(data.length() - 1)
            val orderMoney: String = orderJsonObject.get("money").toString()
            val orderDate: String = orderJsonObject.get("date").toString()
            Log.e("orderJson", "Money: $orderMoney")
            todayOrder = Order("140.6", "12", orderDate)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}