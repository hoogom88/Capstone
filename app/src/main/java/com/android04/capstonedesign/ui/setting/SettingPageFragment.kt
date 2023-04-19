package com.android04.capstonedesign.ui.setting

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.android04.capstonedesign.R
import com.android04.capstonedesign.common.BOTH
import com.android04.capstonedesign.common.NETWORK_ONLY
import com.android04.capstonedesign.common.WIFI_ONLY
import com.android04.capstonedesign.common.isServiceRunning
import com.android04.capstonedesign.data.dto.LogSettingDTO
import com.android04.capstonedesign.databinding.DialogSubUnsubProductBinding
import com.android04.capstonedesign.databinding.FragmentSettingBinding
import com.android04.capstonedesign.ui.base.BaseFragment
import com.android04.capstonedesign.ui.login.IntroActivity
import com.android04.capstonedesign.ui.service.LogService
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// 설정 화면 프래그먼트

@AndroidEntryPoint
class SettingPageFragment :
    BaseFragment<FragmentSettingBinding, SettingPageViewModel>(R.layout.fragment_setting) {
    override val viewModel: SettingPageViewModel by viewModels()
    private val timeArray = Array<Int>(24) { it }
    private val networkTypeArray = arrayOf(NETWORK_ONLY, WIFI_ONLY, BOTH)
    private lateinit var dialog: AlertDialog
    private lateinit var dialogBinding: DialogSubUnsubProductBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        setObserver()
        loadLogSetting()
        setDialog()
    }

    private fun setDialog() {
        dialogBinding = DialogSubUnsubProductBinding.inflate(layoutInflater)
        dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
    }

    private fun showDialog() {
        dialogBinding.apply {
            tvTitle.text = "Log-out"
            tvDetail.text = "After log-out, all saved logs are deleted and Log collection will be terminated"
            btnApprove.setOnClickListener {
                dialog.dismiss()
                stopLogService()
                logOut()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun setObserver() {
        viewModel.logSetting.observe(viewLifecycleOwner) {
            binding.apply {
                spTime.setSelection(timeArray.indexOf(it.postTime))
//                spNetwork.setSelection(networkTypeArray.indexOf(it.networkType))
            }
        }
    }

    private fun initBinding() {
        val timeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, timeArray)
        val networkTypeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            networkTypeArray
        )

        binding.apply {
            spTime.adapter = timeAdapter
            spNetwork.adapter = networkTypeAdapter
//            tvPostTimeTitle.visibility = if (App.loginType == LoginType.SELLER.code) View.VISIBLE else View.GONE
//            spTime.visibility = if (App.loginType == LoginType.SELLER.code) View.VISIBLE else View.GONE
            btnLogout.setOnClickListener {
                showDialog()
            }
            spTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.d(TAG, "spTime: $position, ${spTime.selectedItem}")
                    changeLogSetting()
                }
            }
            spNetwork.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.d(TAG, "spNetwork: $position, ${spNetwork.selectedItem}")
                    changeLogSetting()
                }
            }
            srlBase.setOnRefreshListener {
                srlBase.isRefreshing = false
            }

        }
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(requireContext(), "Log-out complete", Toast.LENGTH_SHORT).show()
        viewModel.logOut()
        moveToLogIn()
    }

    private fun stopLogService() {
        if (requireContext().isServiceRunning<LogService>()) {
            Log.d(TAG, "로그 서비스 실행중 -> 종료")
            val intent = Intent(requireContext(), LogService::class.java)
            requireContext().stopService(intent)
        }
    }

    private fun moveToLogIn() {
        val intent = Intent(requireContext(), IntroActivity::class.java)
        requireContext().startActivity(intent)
        activity?.finish()
    }

    private fun changeLogSetting() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.changeLogSetting(LogSettingDTO(timeArray[binding.spTime.selectedItemPosition], networkTypeArray[binding.spNetwork.selectedItemPosition]))
        }
    }

    private fun loadLogSetting() {
        viewModel.loadLogSetting()
    }

    companion object {
        const val TAG = "SettingPageFragment"
    }
}