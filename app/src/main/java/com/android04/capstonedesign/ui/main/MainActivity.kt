package com.android04.capstonedesign.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android04.capstonedesign.R
import com.android04.capstonedesign.common.App
import com.android04.capstonedesign.common.LoginType
import com.android04.capstonedesign.common.ServiceNotification
import com.android04.capstonedesign.databinding.ActivityMainBinding
import com.android04.capstonedesign.ui.base.BaseActivity
import com.android04.capstonedesign.ui.home.HomeFragment
import com.android04.capstonedesign.ui.insight.InsightMainActivity
import com.android04.capstonedesign.ui.log.LogPageFragment
import com.android04.capstonedesign.ui.point.PointPageFragment
import com.android04.capstonedesign.ui.product.ProductPageFragment
import com.android04.capstonedesign.ui.setting.SettingPageFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

// Log, Point, Product, Home, Setting 호스트 액티비티

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    override val viewModel: MainViewModel by viewModels()
    private var data = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        initPage()
        initBottomNavigation()
        setNavItemSelected(R.id.navigation_home)
        ServiceNotification.createChannel(this)
//        postTestLog()
        getAppInfoData()
        getAccountInfo()
        storeFileUsingStream()
    }

    private fun storeFileUsingStream() {
        lifecycleScope.launch(Dispatchers.IO) {
            val baseDir = applicationContext.filesDir?.absolutePath ?: ""
            val fileName = "EncKey.bin"
            Log.d(SettingPageFragment.TAG, "storeFileUsingStream(): ${baseDir}")
            File(baseDir, "/key").mkdirs()
            File("$baseDir/key", "/PK").mkdirs()
            val fileOutputStream = FileOutputStream(File(baseDir + "/key/PK", fileName))
            val assetManager = resources.assets
            val inputStream = assetManager.open(fileName)
            fileOutputStream.use {
                if (it != null) {
                    it.write(inputStream.readBytes())
                }
            }
        }
    }

    fun requestBatteryIgnorePermission() {
        val intent = Intent()
        val packageName = packageName
        intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    fun checkBatteryIgnorePermission(): Boolean {
        val packageName = packageName
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(packageName)
    }

    private fun getAccountInfo() {
        viewModel.getAccountInfo()
    }

    private fun initPage() {
        if (App.loginType == LoginType.SELLER.code) {
            binding.navView.inflateMenu(R.menu.seller_bottom_nav_menu)
            getBatteryInfo()
        } else {
            binding.navView.inflateMenu(R.menu.buyer_bottom_nav_menu)
        }

    }

    private fun getAppInfoData() {
        viewModel.getAppInfoData()
    }

    private fun getBatteryInfo() {
//        val tmp: BatteryManager = this.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
//        val c = tmp.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
//        val d = tmp.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
//        Log.d(TAG, "getBatteryInfo(): Calculated-${(c/d*100)}")
        val name = "com.android.internal.os.PowerProfile"
        val profile = Class.forName(name)
            .getConstructor(Context::class.java)
            .newInstance(this)
        val capacity = Class.forName(name)
            .getMethod("getBatteryCapacity")
            .invoke(profile) as Double
        App.batteryCapacity = capacity.toInt()
        Log.i(TAG, "getBatteryInfo(): Class-${capacity}")
    }

    private fun initBottomNavigation() {
        binding.navView.setOnItemSelectedListener { menuItem ->
            if (menuItem.isChecked) {
                false
            } else when (menuItem.itemId) {
                R.id.navigation_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navigation_product -> {
                    val fragment = ProductPageFragment()
                    val bundle = Bundle()
                    bundle.putInt(HomeFragment.NUM, data)
                    fragment.arguments = bundle
                    replaceFragment(fragment)
                    true
                }
                R.id.navigation_point -> {
                    replaceFragment(PointPageFragment())
                    true
                }
                R.id.navigation_setting -> {
                    replaceFragment(SettingPageFragment())
                    true
                }
                R.id.navigation_log -> {
                    replaceFragment(LogPageFragment())
                    true
                }
                R.id.navigation_insight -> {
                    moveToInsightPage()
                    false
                }
                else -> false
            }
        }
        replaceFragment(HomeFragment())
    }

    private fun moveToInsightPage() {
        val intent = Intent(this, InsightMainActivity::class.java)
        startActivity(intent)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main, fragment).commit()
    }

    fun replaceToProductFragment(newData: Int) {
        data = newData
        binding.navView.selectedItemId = R.id.navigation_product
    }

    fun replaceToLogFragment() {
        binding.navView.selectedItemId = R.id.navigation_log
    }

    private fun setNavItemSelected(id: Int) {
        binding.navView.selectedItemId = id
    }

    fun replaceToPointFragment() {
        binding.navView.selectedItemId = R.id.navigation_point
    }

    override fun onResume() {
        super.onResume()
        getAppInfoData()
        getAccountInfo()
    }
    companion object {
        const val TAG = "MainActivityLog"
    }
}