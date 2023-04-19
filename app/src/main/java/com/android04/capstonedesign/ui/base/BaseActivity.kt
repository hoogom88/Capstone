package com.android04.capstonedesign.ui.base

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

// Activity 공통 기능 추상 클래스

abstract class BaseActivity<T : ViewDataBinding, R : ViewModel>(
    @LayoutRes
    private val layoutResID: Int
) : AppCompatActivity() {
    lateinit var binding: T
    abstract val viewModel: R
    private var backPressedTime = System.currentTimeMillis() - 1600

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResID)
        binding.lifecycleOwner = this
    }

    fun showToast(context: Context, @StringRes resourceId: Int) {
        Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (backPressedTime + 1500 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}
