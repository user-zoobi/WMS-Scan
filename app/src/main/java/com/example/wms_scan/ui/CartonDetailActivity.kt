package com.example.wms_scan.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.scanmate.extensions.*
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.pallets
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.rack
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.shelf
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.warehouse
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityCartonDetailBinding
import com.example.wms_scan.utils.PermissionDialog


class CartonDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCartonDetailBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var permissionDialog: PermissionDialog
    private var analyticalNo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartonDetailBinding.inflate(layoutInflater)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setContentView(binding.root)
        permissionDialog = PermissionDialog(this)
        initListener()
        initObserver()
        setupUi()

    }

    private fun initListener(){

        binding.saveBtn.click {


        }

        binding.logout.click {
            clearPreferences(this)
        }

    }

    private fun initObserver(){

    }

    private fun setupUi(){
        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)

        analyticalNo = intent.extras?.getString("Analytical_No").toString()
        val materialId = intent.extras?.getString("material_id")
        val stock = intent.extras?.getString("stock")

        binding.analyticalNumTV.text = analyticalNo
        binding.materialNumTV.text = materialId
        binding.stockTV.text = stock

        binding.userNameTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userName
        )
        binding.userDesignTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userDesignation
        )
        binding.loginTimeTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.loginTime
        )

        binding.WHTV.text = LocalPreferences.getString(this, warehouse)
        binding.rackTV.text =  LocalPreferences.getString(this, rack)
        binding.shelfTV.text =  LocalPreferences.getString(this, shelf)
        binding.palletTV.text =  LocalPreferences.getString(this, pallets)

        when
        {
            intent.extras?.getInt("isExist") == 1 ->{
                binding.updateBtn.visible()
                binding.saveBtn.gone()
            }
        }
    }

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        gotoActivity(LoginActivity::class.java)
    }

    override fun onBackPressed() {
        finish()
    }

}