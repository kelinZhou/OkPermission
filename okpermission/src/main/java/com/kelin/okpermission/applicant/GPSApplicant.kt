package com.kelin.okpermission.applicant

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.os.Build
import com.kelin.okpermission.OkActivityResult
import com.kelin.okpermission.permission.Permission
import com.kelin.okpermission.router.PermissionRouter

/**
 * **描述:** 开启GPS的申请器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/8/14 1:12 PM
 *
 * **版本:** v 1.0.0
 */
class GPSApplicant(activity: Activity, router: PermissionRouter) : PermissionsApplicant(activity, router) {
    override fun checkSelfPermission(permission: Permission): Boolean {
        return isGPSEnable()
    }

    private fun isGPSEnable(): Boolean {
        return (applicationContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager)?.let { lm ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                lm.isLocationEnabled
            } else {
                lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            }
        } ?: false
    }

    override fun shouldShowRequestPermissionRationale(router: PermissionRouter, permission: Permission): Boolean {
        return true
    }

    override fun requestPermissions(router: PermissionRouter, permissions: Array<out Permission>, onResult: (permissions: Array<out Permission>) -> Unit) {
        OkActivityResult.startActivityForCodeOrException(activity, intentGenerator.generatorIntent(activity)) { _, e ->
            if (isGPSEnable()) {
                onResult(emptyArray())
            } else {
                onResult(permissions)
            }
        }
    }
}