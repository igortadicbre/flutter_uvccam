package com.chenyeju

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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

    /**
     * Check if all required permissions are granted.
     * - Always requires CAMERA
     * - Requires RECORD_AUDIO for video capture with mic
     * - For SDK < 29, also requires WRITE_EXTERNAL_STORAGE for saving media to external storage
     */
    fun hasRequiredPermissions(context: Context?): Boolean {
        if (context == null) return false
        val hasCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val hasAudio = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

        val hasStorage = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            // From Android 10 (Q) onwards, scoped storage is used; WRITE_EXTERNAL_STORAGE is deprecated/ignored
            true
        }

        return hasCamera && hasAudio && hasStorage
    }

    /**
     * Request required permissions if not already granted.
     * Returns true if already granted; false if a request was initiated.
     */
    fun requestPermissionsIfNeeded(activity: Activity?): Boolean {
        if (activity == null) return false
        if (hasRequiredPermissions(activity)) return true

        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), REQ)
        return false
    }
}
