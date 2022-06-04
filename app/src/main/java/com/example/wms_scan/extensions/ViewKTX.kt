package com.example.scanmate.extensions

import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.wms_scan.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun EditText.showPassword(isShowPassword: Boolean) {
    this.apply {
        if(isShowPassword) {
            transformationMethod = PasswordTransformationMethod()
            setSelection(text.toString().length)
        } else {
            transformationMethod = null
            setSelection(text.toString().length)
        }
    }
}

fun View.snackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun AppCompatActivity.showDialog(message: String) {
    MaterialAlertDialogBuilder(this)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
            // Respond to positive button press
            dialog.dismiss()
        }
        .show()
}