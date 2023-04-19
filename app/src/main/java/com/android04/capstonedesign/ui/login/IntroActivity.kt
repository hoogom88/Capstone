package com.android04.capstonedesign.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.android04.capstonedesign.R
import com.android04.capstonedesign.common.App
import com.android04.capstonedesign.common.INVALID_DATA
import com.android04.capstonedesign.common.LoginType
import com.android04.capstonedesign.databinding.ActivityIntroBinding
import com.android04.capstonedesign.ui.base.BaseActivity
import com.android04.capstonedesign.ui.main.MainActivity
import com.android04.capstonedesign.ui.main.MainViewModel
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint

// 로그인 화면 액티비티

@AndroidEntryPoint
class IntroActivity : BaseActivity<ActivityIntroBinding, MainViewModel>(R.layout.activity_intro) {
    override val viewModel: MainViewModel by viewModels()
    private val googleSignInOptions: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    private val googleSignIn by lazy {
        GoogleSignIn.getClient(this, googleSignInOptions)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private var tokenId: String? = null
    private val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.i(TAG, "loginLauncher: ${result.resultCode} - ${result.data?.let {
                Auth.GoogleSignInApi.getSignInResultFromIntent(
                    it
                )?.status.toString()
            }}")
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    task.getResult(ApiException::class.java)?.let { account ->
                        tokenId = account.idToken
                        firebaseAuthWithGoogle(account.idToken)
                    } ?: throw Exception()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                binding.lottieLoading.visibility = View.GONE
                btnEnable(true)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkAutoLogin()) return
        binding.apply {
            btnBuyer.setOnClickListener {
                requestLogin(LoginType.BUYER.code)
            }
            btnSeller.setOnClickListener {
                requestLogin(LoginType.SELLER.code)
            }
        }
        viewModel.isLoginComplete.observe(this) {
            if (it == true) {
                Log.i(TAG, "isLoginComplete: new account")
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            } else if(it == false) {
                Log.i(TAG, "isLoginComplete: existed account: ${App.loginType}")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun requestLogin(type: Int) {
        binding.apply {
            lottieLoading.visibility = View.VISIBLE
            btnEnable(false)
            loginLauncher.launch(googleSignIn.signInIntent)
            App.loginType = type
        }
    }

    private fun btnEnable(enable: Boolean) {
        binding.apply {
            btnSeller.isEnabled = enable
            btnBuyer.isEnabled = enable
        }
    }

    private fun checkAutoLogin(): Boolean {
        val info = viewModel.loadLoginInfo()
        if (info.email != INVALID_DATA) {
            App.userEmail = info.email
            App.loginType = info.type
            when (info.type) {
                LoginType.SELLER.code -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    Log.i(TAG, "checkAutoLogin(): seller")
                }
                LoginType.BUYER.code -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    Log.i(TAG, "checkAutoLogin(): buyer")
                }
            }
            return true
        }
        Log.i(TAG, "checkAutoLogin(): false")
        return false
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                Log.i(TAG, "firebaseAuthWithGoogle(: ${task.result}")
                if (task.isSuccessful) {
                    viewModel.saveLoginInfo(firebaseAuth.currentUser?.email ?: INVALID_DATA)
                    viewModel.postUserAccount(firebaseAuth.currentUser?.email ?: INVALID_DATA)
                    Log.d(TAG, firebaseAuth.currentUser?.email ?: INVALID_DATA)
                }
            }
    }

    companion object {
        const val TAG = "LoginActivityLog"
    }

}
