package com.example.wms_scan.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.scanmate.extensions.click
import com.example.scanmate.extensions.gotoActivity
import com.example.scanmate.extensions.obtainViewModel
import com.example.scanmate.extensions.setTransparentStatusBarColor
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityCartonDetailBinding
import com.example.wms_scan.utils.PermissionDialog


class CartonDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCartonDetailBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var permissionDialog: PermissionDialog

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
            permissionDialog.show()
        }
    }

    private fun initObserver(){

    }

    private fun setupUi(){
        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)

        val analyticalNo = intent.extras?.getString("Analytical_No")
        val materialId = intent.extras?.getString("material_id")
        val materialName = intent.extras?.getString("Material_name")
    }

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        gotoActivity(LoginActivity::class.java)
    }

}