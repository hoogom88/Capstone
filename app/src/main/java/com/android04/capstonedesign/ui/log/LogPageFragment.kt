package com.android04.capstonedesign.ui.log

import android.Manifest
import android.app.AlertDialog
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android04.capstonedesign.R
import com.android04.capstonedesign.common.App
import com.android04.capstonedesign.common.ProductType
import com.android04.capstonedesign.common.isServiceRunning
import com.android04.capstonedesign.data.dto.LogData
import com.android04.capstonedesign.data.dto.ProductStatusDTO
import com.android04.capstonedesign.data.room.entity.AppStatsLog
import com.android04.capstonedesign.data.room.entity.LocationLog
import com.android04.capstonedesign.data.room.entity.PostLog
import com.android04.capstonedesign.databinding.DialogSubUnsubProductBinding
import com.android04.capstonedesign.databinding.FragmentLogBinding
import com.android04.capstonedesign.ui.base.BaseFragment
import com.android04.capstonedesign.ui.main.MainActivity
import com.android04.capstonedesign.ui.service.LogService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

// 로그 화면 프래그먼트

@AndroidEntryPoint
class LogPageFragment : BaseFragment<FragmentLogBinding, LogPageViewModel>(R.layout.fragment_log) {
    override val viewModel: LogPageViewModel by viewModels()

    private lateinit var dialog: AlertDialog
    private lateinit var dialogBinding: DialogSubUnsubProductBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        checkProductStatus()
        setObserver()
        fetchLocationLog()
        setDialog()
    }

    private fun setDialog() {
        dialogBinding = DialogSubUnsubProductBinding.inflate(layoutInflater)
        dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
    }

    private fun showLogDetail(data: LogData) {
        val text = generateDetail(data)
        dialogBinding.apply {
            tvDetail.maxLines = 10
            tvTitle.text = "Log detail"
            tvDetail.text = text
            btnApprove.visibility = View.INVISIBLE
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun generateDetail(data: LogData): String {
        var result = StringBuilder()
        if (data.logType == ProductType.LOCATION.code) {
            val item = (data as LocationLog)
            result.append("Save Location log\n\n(${item.latitude}, ${item.longitude})")
        } else if (data.logType == ProductType.APP_USAGE.code) {
            result.append("Save App usage log\n\n")
            val item = (data as AppStatsLog)
            val appUsage = item.data.split(",")
            var cnt = 0
            for (idx in App.nameList.indices) {
                if (appUsage[idx] != "0") {
                    result.append("${App.nameList[idx]}-${appUsage[idx]}m, ")
                    cnt++
                }
            }
            if (cnt == 0) result.append("App usage log is empty") else result.removeRange(result.length-2, result.length)
        } else {
            val item = (data as PostLog)
            result.append(item.data)
        }
        return result.toString()
    }

    private fun showAppStatsPermissionDialog() {
        dialogBinding.apply {
            tvDetail.maxLines = 5
            tvTitle.text = "Request permission"
            tvDetail.text = "Permissions are required for log collection.\nPlease allow permission and try again.\n\nLogCatcher -> Permit usage access -> allow"
            btnApprove.setOnClickListener {
                requireContext().startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                binding.switchLog.isChecked = false
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                binding.switchLog.isChecked = false
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showBatteryPermissionDialog() {
        dialogBinding.apply {
            tvDetail.maxLines = 5
            tvTitle.text = "Battery Setting"
            tvDetail.text = "If you do not turn off battery optimization mode, log collection may not function normally."
            btnApprove.setOnClickListener {
                (activity as MainActivity).requestBatteryIgnorePermission()
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun checkBatteryPermission() {
        Log.i(TAG, "checkBatteryPermission")
        if (!(activity as MainActivity).checkBatteryIgnorePermission()) {
            showBatteryPermissionDialog()
        }
    }

    private fun setObserver() {
        viewModel.apply {
            logData.observe(viewLifecycleOwner) {
                (binding.rvLogHistory.adapter as LogLocationRecyclerAdapter).submitList(it)
            }
        }
    }

    private fun fetchLocationLog(){
        viewModel.fetchLogData()
    }

    private fun checkAppStatsPermission() {
        Log.i(TAG, "checkAppStatsPermission()")
        if (!isAppStatsGrantedPermission(requireContext())) showAppStatsPermissionDialog()
        else {
            startLogService()
            checkBatteryPermission()
        }
    }

    private fun isAppStatsGrantedPermission(context: Context): Boolean {
        var result = false
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
        } else {
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
        }
        if (mode == AppOpsManager.MODE_DEFAULT) {
            result = context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
        } else {
            result = (mode == AppOpsManager.MODE_ALLOWED)
        }
        Log.d(TAG, "isAppStatsGrantedPermission(): 권한 $result")
        return result
    }

    private fun initBinding() {
        binding.viewModel = viewModel
        binding.apply {
            switchLog.setOnCheckedChangeListener { _, isChecked ->
                switchOnOff(isChecked)
            }
            srlBase.setOnRefreshListener {
                fetchLocationLog()
                srlBase.isRefreshing = false
            }
            rvLogHistory.adapter = LogLocationRecyclerAdapter()
            (rvLogHistory.adapter as LogLocationRecyclerAdapter).setOnItemCheckListener( object :
                LogLocationRecyclerAdapter.OnItemCheckListener {
                    override fun onItemClick(pos: Int) {
                        showLogDetail((rvLogHistory.adapter as LogLocationRecyclerAdapter).currentList[pos])
                    }
                }
            )
            lottieProcessing.speed = 0.5F
        }
    }

//    private fun checkPermission(): Boolean {
//        var granted = false
//        val appOps = requireContext()
//            .getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
//        val mode = appOps.checkOpNoThrow(
//            AppOpsManager.OPSTR_GET_USAGE_STATS,
//            Process.myUid(), requireContext().packageName
//        )
//        granted = if (mode == AppOpsManager.MODE_DEFAULT) {
//            requireContext().checkCallingOrSelfPermission(
//                Manifest.permission.PACKAGE_USAGE_STATS
//            ) == PackageManager.PERMISSION_GRANTED
//        } else {
//            mode == AppOpsManager.MODE_ALLOWED
//        }
//        return granted
//    }

    override fun onResume() {
        super.onResume()
        checkProductStatus()
        if (context?.isServiceRunning<LogService>() == true) {
            binding.switchLog.isChecked = true
            viewModel.setLottie(true)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkLocationPermission()
    }

    private fun switchOnOff(isChecked: Boolean){
        Log.d(TAG, "switchOnOff(): $isChecked")
        if (isChecked && !requireContext().isServiceRunning<LogService>()) {
            CoroutineScope(Dispatchers.IO).launch {
                delay(1000)
                withContext(Dispatchers.Main) {
                    requestStartService()
                }
            }
        } else if (!isChecked && requireContext().isServiceRunning<LogService>()){
            stopLogService()
        }

    }

    private fun requestStartService() {
        val status = viewModel.productStatus.value?:  ProductStatusDTO()
        lifecycleScope.launch {
            delay(1000)
            if (!status.location && !status.appInfo) {
                Toast.makeText(requireContext(), "There are no accessible logs.\nSubscribe product first", Toast.LENGTH_SHORT).show()
                binding?.switchLog.isChecked = false
            } else {
                if (status.appInfo) checkAppStatsPermission()
                else {
                    checkBatteryPermission()
                    startLogService()
                }
            }
        }
    }

    private fun startLogService() {
        val status = viewModel.productStatus.value?: ProductStatusDTO()
        val intent = Intent(requireContext(), LogService::class.java)
        intent.putExtra(LogService.STATUS, status)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= 26) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
        viewModel.setLottie(true)
    }

    private fun checkProductStatus() {
        viewModel.loadProductStatus()
    }

    private fun stopLogService(){
        val intent = Intent(requireContext(), LogService::class.java)
        requireContext().stopService(intent)
        viewModel.setLottie(false)
    }

    private fun checkLocationPermission() {
        Log.i(TAG, "checkLocationPermission()")
        if (isGrantedLocationPermission(requireContext())) {
            Log.d(TAG, "checkLocationPermission(): 권한 있음")
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                showToast(requireContext(), "Unable to run location log collection on permission rejection", false)
            }
            Log.d(TAG, "checkLocationPermission(): 권한 없음")
            requestLocationPermission()
        }
    }

    private fun isGrantedLocationPermission(context: Context) =
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        var permissionCount = 0
        val permissionManager = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(
            )
        ) { permissions ->
            permissions.entries.forEach {
                if (it.value) permissionCount++
            }
            if (permissionCount == 2) {
                Log.d(TAG, "requestLocationPermission(): 권한 2개")
            } else {
                Log.d(TAG, "requestLocationPermission(): 권한 0개")
            }
        }
        permissionManager.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    companion object {
        const val TAG = "LogPageFragmentLog"
    }
}