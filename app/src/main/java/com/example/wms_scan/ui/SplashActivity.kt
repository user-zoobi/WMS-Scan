package com.example.wms_scan.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatDelegate
import com.example.scanmate.extensions.gotoActivity
import com.example.scanmate.extensions.setTransparentStatusBarColor
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.isLogin
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivitySplashBinding
import com.example.wms_scan.utils.TextureVideoView
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
        configureVideoView()

        val code = "01L-01WH-01S-01R-01P"

        if (code.contains("L"))
        {
            Log.i("Zohaib", "Location: ${code.substringBefore("L-")}L")
        }

        if (code.contains("WH"))
        {
            Log.i("Zohaib", "Warehouse: ${code.substringAfter("L-").substringBefore("WH")}WH")
        }

        if ( code.contains("R") ){
            Log.i("Zohaib", "Rack: ${code.substringAfter("S-").substringBefore("R")}R")
        }
        if (code.contains("S"))
        {
            Log.i("Zohaib", "Rack: ${code.substringAfter("H-").substringBefore("S")}S")
        }
        if ( code.contains("P")){
            Log.i("Zohaib", "Rack: ${code.substringAfter("R-").substringBefore("P")}P")
        }


////        when
////        {
////
////
////             ->
////
////             ->
////                Log.i("Zohaib", "Shelve: ${code.substringAfter("WH-").substringBefore("S")}S")
//
////        }
//
//
//        Log.i("Zohaib", "onCreate: ${code.substringBefore("-")}")
    }

    private fun handler(){

        CoroutineScope(Dispatchers.Main).launch {

            Handler(Looper.getMainLooper()).postDelayed({
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
            }, 6000)
        }
    }

    @SuppressLint("ServiceCast")
    private fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnectedOrConnecting
    }

    private fun configureVideoView() {

        binding.videoSplash.setScaleType(TextureVideoView.ScaleType.CENTER_CROP)
        binding.videoSplash.setDataSource(this, Uri.parse("android.resource://" + packageName + "/" + R.raw.splash_black))
        binding.videoSplash.setLooping(true)

    }

    override fun onResume() {
        super.onResume()
        binding.videoSplash.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.videoSplash.stop()
    }

    override fun onPause() {
        super.onPause()
        binding.videoSplash.pause()
    }



}