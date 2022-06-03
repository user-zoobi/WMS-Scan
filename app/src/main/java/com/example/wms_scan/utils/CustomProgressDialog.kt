package com.example.scanmate.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.example.scanmate.R
import com.example.scanmate.databinding.LoadingLayoutBinding

class CustomProgressDialog(context: Context) : Dialog(context, R.style.customDialog) {
    init {
        val binding: LoadingLayoutBinding =
            LoadingLayoutBinding.inflate(LayoutInflater.from(context))
        setTitle(null)
        setCancelable(false)
        setOnCancelListener(null)
        //window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.root)
    }
}