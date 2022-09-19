package com.kelin.okpermission.applicant

import android.app.Activity
import android.os.Build
import android.provider.Settings
import com.kelin.okpermission.OkActivityResult
import com.kelin.okpermission.permission.Permission
import com.kelin.okpermission.router.PermissionRouter


/**
 * **描述:** 系统设置权限的申请器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/8/14 10:25 AM
 *
 * **版本:** v 1.0.0
 */
class WriteSettingsApplicant(activity: Activity, router: PermissionRouter) : PermissionsApplicant(activity, router) {
    override fun checkSelfPermission(permission: Permission): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(applicationContext)
    }

    override fun shouldShowRequestPermissionRationale(router: PermissionRouter, permission: Permission): Boolean {
        return true
    }

    override fun requestPermissions(router: PermissionRouter, permissions: Array<out Permission>, onResult: (permissions: Array<out Permission>) -> Unit) {
        OkActivityResult.startActivityOrException(activity, intentGenerator.generatorIntent(activity)) { _, e ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(applicationContext)) {
                onResult(emptyArray())
            } else {
                onResult(permissions)
            }
        }
    }
}