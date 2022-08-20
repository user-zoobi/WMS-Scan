package com.example.wms_scan.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.scanmate.data.callback.Status
import com.example.scanmate.extensions.*
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppConstants.orgBusLocNo
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.PREF
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.scanCarton
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.userNo
import com.example.scanmate.util.Utils
import com.example.scanmate.util.Utils.isNetworkConnected
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityMenuBinding
import kotlin.system.exitProcess


class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private var cameraRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)

        setupUi()
        initObserver()
        permissions()

    }


    override fun onBackPressed() {
        finishAffinity()
    }

    private fun permissions()
    {
        if(
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        )
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),cameraRequestCode)
        }
    }

    private fun setupUi() {

        supportActionBar?.hide()
        dialog = CustomProgressDialog(this)
        setTransparentStatusBarColor(R.color.transparent)

        with(binding)
        {
            userNameTV.text = LocalPreferences.getString(this@MenuActivity,
                LocalPreferences.AppLoginPreferences.userName
            )

            userDesignTV.text = LocalPreferences.getString(this@MenuActivity,
                LocalPreferences.AppLoginPreferences.userDesignation
            )

            loginTimeTV.text = LocalPreferences.getString(this@MenuActivity,
                LocalPreferences.AppLoginPreferences.loginTime
            )
        }

        initListeners()

    }

    private fun initObserver(){

        /**
         *  User location api observer
         */
        viewModel.userLocation(
            Utils.getSimpleTextBody(LocalPreferences.getInt(this, userNo).toString())
        )
        viewModel.userLoc.observe(this, Observer {

            when(it.status){
                Status.LOADING -> {
                    dialog.show()
                    dialog.setCanceledOnTouchOutside(true)
                    binding.swipeRefresh.isRefreshing = true
                }
                Status.SUCCESS ->{
                    binding.swipeRefresh.isRefreshing = false
                    if(it.data?.get(0)?.status == true)
                    {
                        dialog.dismiss()
                        it.data[0].busLocationName?.let { it1 -> Log.i("Response", it1) }
                        it.data[0].busLocationName?.let { it1 ->
                            LocalPreferences.put(this,orgBusLocNo, it1)
                        }
                    }
                    else
                    {
                    }
                    Log.i("success",it.data?.get(0)?.error.toString())
                }
                Status.ERROR ->{
                    dialog.dismiss()
                    binding.swipeRefresh.isRefreshing = false
                }
            }})

        /**
         *  User Menu api observer
         */
        viewModel.userMenu.observe(this){
            when(it.status){
                Status.LOADING -> dialog.show()

                Status.SUCCESS ->{
                    binding.swipeRefresh.isRefreshing = false
                    if(it.data?.get(0)?.status == true)
                    {
                        dialog.dismiss()
                        Log.i("businessLoc1", it.data[0].menu!!)
                    }
                    else
                    {
                    }
                }
                Status.ERROR ->{
                    binding.swipeRefresh.isRefreshing = false
                    dialog.dismiss()
                }

            }
        }
    }

    private fun initListeners() {

        with(binding)
        {

            toolbar.click {
                clearPreferences(this@MenuActivity)
            }

            warehouseIV.setOnClickListener {
                if (isNetworkConnected(this@MenuActivity)){
                    gotoActivity(WarehouseActivity::class.java, "warehouseValues",false)
                }
            }

            racksIV.setOnClickListener {
                if (isNetworkConnected(this@MenuActivity)){
                    gotoActivity(RacksActivity::class.java)
                }
            }

            shelfIV.setOnClickListener {

                if (isNetworkConnected(this@MenuActivity)) {
                    gotoActivity(ShelfActivity::class.java)
                }
            }

            palletsIV.setOnClickListener {

                if (isNetworkConnected(this@MenuActivity)){
                    gotoActivity(PalletsActivity::class.java)
                }
            }

            placeCartonIV.setOnClickListener {
                if (isNetworkConnected(this@MenuActivity)){
                    gotoActivity(CreateCartonActivity::class.java, "placeCarton",true)
                }
            }

            scanCartonIV.click {
                if (isNetworkConnected(this@MenuActivity)){
                    gotoActivity(ScannerActivity::class.java,scanCarton,true)
                }
            }

            swipeRefresh.setOnRefreshListener {
                viewModel.userLocation(
                    Utils.getSimpleTextBody(LocalPreferences.getInt(this@MenuActivity, userNo).toString())
                )
            }

        }
    }


    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        gotoActivity(LoginActivity::class.java)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    override fun onResume() {
        super.onResume()
        binding.swipeRefresh.isRefreshing = false
    }

}