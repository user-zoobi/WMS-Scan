package com.example.boschscan.extensions

import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.openActivity(isFinish: Boolean = false) {
    startActivity(Intent(requireContext(), T::class.java))
    if(isFinish) {
        activity?.finish()
    }
}

inline fun <reified T> Fragment.openActivityWithStackClear() {
    val intent = Intent(requireContext(), T::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
    activity?.finish()
}

fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}