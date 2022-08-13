package com.example.wms_scan.ui

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.scanmate.data.callback.Status
import com.example.scanmate.extensions.*
import com.example.scanmate.util.Constants.Toast.noRecordFound
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.scanCarton
import com.example.scanmate.util.Utils.isNetworkConnected
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityScannerBinding


class ScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScannerBinding
    private var cameraRequestCode = 100
    private lateinit var viewModel:MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private var analOrMatInput =  ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),cameraRequestCode)
        }
        setupUi()
        initListeners()
        initObserver()

    }

    private fun setupUi(){

        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
        viewModel = obtainViewModel(MainViewModel::class.java)
        dialog = CustomProgressDialog(this)

        when {
            intent.extras?.getBoolean(scanCarton) == true -> {
                binding.loginBtn.hide()
            }
        }

    }

    private fun initObserver()
    {
        viewModel.getCartonQnWise.observe(this){
            when(it.status)
            {
                Status.LOADING ->
                {
                    dialog.show()
                }
                Status.SUCCESS ->
                {
                    if (isNetworkConnected(this))
                    {
                        it.let {
                            try
                            {
                                if (it.data?.get(0)?.status == true)
                                {
                                    dialog.hide()
                                    val intent = Intent(this, ShowAllHierarchy::class.java)
                                    intent.putExtra("manualMatName",analOrMatInput )
                                    intent.putExtra("manualAnalyticalKey",true)
                                    startActivity(intent)
                                }
                                else
                                {
                                  toast(noRecordFound)
                                }
                            }
                            catch (e:Exception)
                            {
                                Log.i("inputManual","${e.message}")
                                Log.i("inputManual","${e.stackTrace}")
                            }
                        }
                    }
                }
                Status.ERROR ->
                {
                    dialog.hide()
                }
            }
        }
    }

    private fun initListeners(){

        binding.scanBtn.click {
            if (isNetworkConnected(this))
            {
                gotoActivity(ScannerCameraActivity::class.java)
            }
            else
            {
                toast("No internet")
            }

        }

        binding.searchManualTV.click {

            binding.scanOptionCont.gone()
            binding.searchManualTV.gone()
            binding.searchScanTV.visible()
            binding.manualOptionCont.visible()
            binding.scanHeaderTv.text = "Type Goods Manually"
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        }

        binding.searchScanTV.click {

            binding.scanOptionCont.visible()
            binding.searchManualTV.visible()
            binding.searchScanTV.gone()
            binding.manualOptionCont.gone()
            binding.scanHeaderTv.text = "Scan Goods"
            //
        }

        binding.loginBtn.click {
            gotoActivity(LoginActivity::class.java)
        }

        binding.backBtn.click {
            onBackPressed()
        }

        binding.searchBtn.click {

            if (isNetworkConnected(this)) {

      ////           input of analytical num or material name

                if (binding.numOrMatNameTV.text.toString().isNullOrEmpty())
                {
                    var analOrMatInput = binding.numOrMatNameTV.text.toString()
                    toast("No record found")
                    Log.i("matInput",analOrMatInput)
                }
                else {
                    analOrMatInput = binding.numOrMatNameTV.text.toString()
                    viewModel.getCartonQnWise(analOrMatInput)
                }
            }

            else
            {
                toast("No internet found")
            }
        }

    }


    override fun onBackPressed() {
        finish()
    }
}