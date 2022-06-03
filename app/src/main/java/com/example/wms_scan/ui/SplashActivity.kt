package com.example.wms_scan.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import com.example.scanmate.extensions.gotoActivity
import com.example.scanmate.extensions.setTransparentStatusBarColor
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.isLogin
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivitySplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        handler()
    }

    private fun handler(){

        CoroutineScope(Dispatchers.Main).launch {

            Handler().postDelayed({
                if (LocalPreferences.getBoolean(this@SplashActivity, isLogin)){
                    if (isNetworkConnected(this@SplashActivity)){
                        gotoActivity(MenuActivity::class.java)
                        finish()
                    }else{
                        gotoActivity(NoNetworkActivity::class.java)
                        finish()
                    }
                }else{
                    if (isNetworkConnected(this@SplashActivity)){
                        gotoActivity(ScannerActivity::class.java)
                        finish()
                    }else{
                        gotoActivity(NoNetworkActivity::class.java)
                        finish()
                    }
                }
            }, 3000)
        }
    }

    @SuppressLint("ServiceCast")
    private fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnectedOrConnecting
    }


}