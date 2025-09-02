package com.chenyeju

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionManager {
    private const val REQ = 1230

    fun hasCameraPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED

    fun requestCameraIfNeeded(activity: Activity?): Boolean {
        if (activity == null) return false
        if (hasCameraPermission(activity)) return true
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), REQ)
        return false
    }

    fun isPermissionGranted(requestCode: Int, grantResults: IntArray): Boolean =
        requestCode == REQ && grantResults.isNotEmpty() &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
}
