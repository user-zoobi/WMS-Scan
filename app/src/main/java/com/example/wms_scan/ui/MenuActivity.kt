package com.example.wms_scan.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.scanmate.data.callback.Status
import com.example.scanmate.data.response.UserLocationResponse
import com.example.scanmate.extensions.*
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppConstants.orgBusLocNo
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.PREF
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.busLocNo
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initObserver()

    }


    override fun onBackPressed() {
        finishAffinity()
        overridePendingTransition(R.anim.slide_bottom, R.anim.slide_up)
        exitProcess(0)
    }

    private fun setupUi() {

        supportActionBar?.hide()
        dialog = CustomProgressDialog(this)
        setTransparentStatusBarColor(R.color.transparent)

        binding.toolbar.click {
            clearPreferences(this)
        }
        binding.userNameTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userName
        )
        binding.userDesignTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userDesignation
        )
        binding.loginTimeTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.loginTime
        )

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
                }
                Status.SUCCESS ->{
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
                     toast("no result found")
                    }
                }
                Status.ERROR ->{
                    dialog.dismiss()
                }
            }})

        /**
         *  User Menu api observer
         */
        viewModel.userMenu.observe(this, Observer {
            when(it.status){
                Status.LOADING -> dialog.show()
                Status.SUCCESS ->{
                    if(it.data?.get(0)?.status == true)
                    {
                        dialog.dismiss()
                        Log.i("businessLoc1", it.data[0].menu!!)
                    }
                    else
                    {
                        toast("no result found")
                    }
                }
                Status.ERROR -> dialog.dismiss()
            }
        })
    }

    private fun initListeners() {
        binding.warehouseIV.setOnClickListener {
            if (isNetworkConnected(this)){
                gotoActivity(WarehouseActivity::class.java, "warehouseValues",false)
            }
            else
            {
                toast("No internet found")
            }

        }
        binding.racksIV.setOnClickListener {
            if (isNetworkConnected(this)){
                gotoActivity(RacksActivity::class.java)
            }
            else
            {
                toast("No internet found")
            }

        }
        binding.shelfIV.setOnClickListener {

            if (isNetworkConnected(this)){
                gotoActivity(ShelfActivity::class.java)
            }
            else
            {
                toast("No internet found")
            }
        }
        binding.palletsIV.setOnClickListener {

            if (isNetworkConnected(this)){
                gotoActivity(PalletsActivity::class.java)
            }
            else
            {
                toast("No internet found")
            }
        }
        binding.placeCartonIV.setOnClickListener {
            if (isNetworkConnected(this)){
                gotoActivity(CreateCartonActivity::class.java, "placeCarton",true)
            }
            else
            {
                toast("No internet found")
            }

        }
        binding.scanCartonIV.click {
            if (isNetworkConnected(this)){
                gotoActivity(ScannerActivity::class.java,scanCarton,true)
            }
            else
            {
                toast("No internet found")
            }
        }

    }


    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        gotoActivity(LoginActivity::class.java)
    }


}