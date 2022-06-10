package com.example.wms_scan.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.example.scanmate.data.callback.Status
import com.example.scanmate.extensions.*
import com.example.scanmate.util.Constants
import com.example.scanmate.util.Constants.Toast.NoInternetFound
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.Utils
import com.example.scanmate.util.Utils.isNetworkConnected
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityAddUpdateCartonBinding
import com.example.wms_scan.databinding.ActivityMenuBinding

class AddUpdateCarton : AppCompatActivity() {
    private lateinit var binding: ActivityAddUpdateCartonBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateCartonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
    }

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        finish()
    }

    override fun onBackPressed() {
        finish()
    }

}