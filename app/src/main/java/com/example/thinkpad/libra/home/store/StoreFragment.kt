package com.example.thinkpad.libra.home.store

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thinkpad.libra.R
import com.example.thinkpad.libra.login.LoginActivity
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
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
        initView()
    }

    private fun initView() {
        exit_linearLayout.setOnClickListener { exitHome() }
        about_linearLayout.setOnClickListener { showAboutUsInfoDialog() }
        setting_linearLayout.setOnClickListener { showSettingInfoDialog() }
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
}