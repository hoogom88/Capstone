package com.android04.capstonedesign.ui.login

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.android04.capstonedesign.R
import com.android04.capstonedesign.databinding.ActivityLoginBinding
import com.android04.capstonedesign.ui.base.BaseActivity
import com.android04.capstonedesign.ui.main.MainViewModel
import com.android04.capstonedesign.ui.service.AppPostWorker
import com.android04.capstonedesign.ui.service.LocationPostWorker
import com.android04.capstonedesign.ui.service.LogService
import com.android04.capstonedesign.util.EncryptDataGenerator
import com.android04.capstonedesign.util.LogTest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

// 테스트용 액티비티, 사용 X

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, MainViewModel>(R.layout.activity_login) {
    override val viewModel: MainViewModel by viewModels()
    private val tester = LogTest()
    private val encryptor = EncryptDataGenerator()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            btnPostAppLog.setOnClickListener {
                postAppLog()
            }
            btnPostLocationLog.setOnClickListener {
                postLocationLog()
            }
        }

    }

    private fun postAppLog() {
        lifecycleScope.launch(Dispatchers.IO) {
            val cnt = encryptor.encryptAppData(tester.createAppDummyData())
//            val cnt = encryptor.encryptAppData(tester.createAppRandomDummyData(), binding.tvGender.text.toString(), binding.tvAge.text.toString())
            Log.d(TAG, "test App: $cnt")
            val data = Data.Builder()
            data.putInt(LogService.LOG_COUNT, cnt)
            val workRequest = OneTimeWorkRequestBuilder<AppPostWorker>()
                .setInputData(data.build())
                .build()
            WorkManager.getInstance(this@LoginActivity).enqueueUniqueWork(LogService.TAG, ExistingWorkPolicy.REPLACE, workRequest)
        }
    }

    private fun postLocationLog() {
        lifecycleScope.launch(Dispatchers.IO) {
            val cnt = encryptor.encryptLocationData(tester.createLocationDummyData())
//            val cnt = encryptor.encryptLocationData(tester.createLocationRandomDummyData(), binding.tvGender.text.toString(), binding.tvAge.text.toString())
            Log.d(TAG, "test Location: $cnt")
            val data = Data.Builder()
            data.putInt(LogService.LOG_COUNT, cnt)
            val workRequest = OneTimeWorkRequestBuilder<LocationPostWorker>()
                .setInputData(data.build())
                .build()
            WorkManager.getInstance(this@LoginActivity).enqueueUniqueWork(LogService.TAG, ExistingWorkPolicy.REPLACE, workRequest)
        }
    }

    private fun storeFileUsingStream() {
        val baseDir = this.filesDir?.absolutePath ?: ""
        val fileName = "EncKey.bin"
        Log.d(TAG, "storeFileUsingStream(): ${baseDir}")
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

    fun loadFile(name: String): ByteArray {
        val dir = this.filesDir
        val data = File(dir, name).readBytes()
        return data
    }

    external fun execute(data: IntArray, num: Int): Int

    override fun onBackPressed() {
        finish()
    }

    companion object {
        const val TAG = "TestActivityLog"
        init {
            System.loadLibrary("capstonedesign")
        }
    }

}
