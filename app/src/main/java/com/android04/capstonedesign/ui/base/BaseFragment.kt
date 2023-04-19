package com.android04.capstonedesign.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

// Fragment 공통 기능 추상 클래스

abstract class BaseFragment<T : ViewDataBinding, R : ViewModel>(@LayoutRes private val layoutResID: Int) :
    Fragment() {

    protected var _binding: T? = null
    protected val binding get() = _binding!!

    abstract val viewModel: R

    fun setStatusBarColor(colorId: Int) {
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), colorId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResID, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        Log.d("fragmentLifecycle", "${this}, onDestroy")
    }

    fun showToast(context: Context, @StringRes resourceId: Int) {
        Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_SHORT).show()
    }

    fun showToast(context: Context, message: String, isShort: Boolean) {
        val length = if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
        Toast.makeText(context, message, length).show()
    }

}
