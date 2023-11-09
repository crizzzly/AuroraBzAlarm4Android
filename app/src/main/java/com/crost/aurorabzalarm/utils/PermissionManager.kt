package com.crost.aurorabzalarm.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionManager {
    private val _permissionState = MutableStateFlow(false)
    val permissionState: StateFlow<Boolean> = _permissionState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val permissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NOTIFICATION_POLICY,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.FOREGROUND_SERVICE,
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun hasPermission(context: Context): Boolean {
        _permissionState.value = true
        for (permission in permissions) {
            val result = ContextCompat.checkSelfPermission(context, permission)
            Log.d("hasPermissionLegacy", "$permission hasPermission: $result")
            if (result != PackageManager.PERMISSION_GRANTED) {
                _permissionState.value = false
                return _permissionState.value
            }
        }
        Log.d("PermissionManager", "permissionState: ${_permissionState.value}")
        return _permissionState.value
    }

    //TODO: Set MinSdk to Tiramisu

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermission(
        context: Context,
        permissionLauncher: ActivityResultLauncher<Array<String>>
    ): Boolean {
//        Log.d("requestPermission", "requesting permission")
        // Check if the permission is already granted
        if (!hasPermission(context)) {
            _permissionState.value = false
            // Permission is not granted, request it
//            Log.d(
//                "requestPermission",
//                "permission is not granted. Requesting permission"
//            )
            permissionLauncher.launch(permissions)
        }
        val permission = hasPermission(context)
//        Log.d("hasPermission", "Value = $permission")

        return permission
    }

    fun onPermissionGranted() {
        // Update the permission state flow when the permission is granted
//        Log.i("onPermissionGranted", "permissionState.value set to true")
        _permissionState.value = true
    }
}