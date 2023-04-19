package com.android04.capstonedesign.ui.login

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.android04.capstonedesign.R
import com.android04.capstonedesign.common.App
import com.android04.capstonedesign.common.LoginType
import com.android04.capstonedesign.databinding.ActivitySignupBinding
import com.android04.capstonedesign.ui.base.BaseActivity
import com.android04.capstonedesign.ui.main.MainActivity
import com.android04.capstonedesign.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.util.*

// 회원가입(사용자 정보 입력) 화면 액티비티

@AndroidEntryPoint
class SignUpActivity :
    BaseActivity<ActivitySignupBinding, MainViewModel>(R.layout.activity_signup) {
    override val viewModel: MainViewModel by viewModels()
    private var birthYear = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
        initBinding()
        setObserver()

    }

    private fun setObserver() {
        viewModel.isSignUpComplete.observe(this) {
            if (it) {
                when (App.loginType) {
                    LoginType.SELLER.code -> {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        Log.i(TAG, "SignUpComplete(): seller")
                    }
                    LoginType.BUYER.code -> {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        Log.i(TAG, "SignUpComplete(): buyer")
                    }
                }
            }
        }
    }

    private fun initBinding() {
        binding.apply {
            btnSignUp.setOnClickListener {
                btnSignUp.isEnabled = false
                viewModel.signUp(App.userEmail, spGender.selectedItem as String, birthYear)
            }
            tvAge.setOnClickListener {
                val cal = Calendar.getInstance()
                val dateSetListener =
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        val df = DecimalFormat("00")
                        tvAge.text = "${year}.${df.format(month + 1)}.${df.format(dayOfMonth)}"
                        Log.d("createMeeting", "${year}.${month + 1}.${dayOfMonth}")
                        birthYear = 2022 - year + 1
                    }
                DatePickerDialog(
                    this@SignUpActivity, dateSetListener, cal.get(Calendar.YEAR), cal.get(
                        Calendar.MONTH
                    ), cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }

    private fun initPage() {
        binding.apply {
            spGender.adapter = ArrayAdapter<String>(
                this@SignUpActivity,
                android.R.layout.simple_spinner_item,
                listOf("Male", "Female")
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }
    }

    companion object {
        const val TAG = "SignUpActivityLog"
    }

}
