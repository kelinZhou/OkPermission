package com.kelin.okpermission.applicant

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.kelin.okpermission.OkActivityResult
import com.kelin.okpermission.intentgenerator.ApkInstallPermissionIntentGenerator
import com.kelin.okpermission.permission.Permission
import com.kelin.okpermission.router.PermissionRequestRouter

/**
 * **描述:** Apk安装权限申请器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-15  16:30
 *
 * **版本:** v 1.0.0
 */
class ApkInstallApplicant(context: Activity) : PermissionsApplicant(context) {

    internal var apkInstallPermissionIntentGenerator: ApkInstallPermissionIntentGenerator? = null

    override fun checkSelfPermissions(permission: Permission): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.O || context.packageManager.canRequestPackageInstalls()
    }

    override fun shouldShowRequestPermissionRationale(
        router: PermissionRequestRouter,
        permission: Permission
    ): Boolean {
        return true
    }

    override fun requestPermissions(
        router: PermissionRequestRouter,
        permissions: Array<out Permission>,
        onResult: (permissions: Array<out Permission>) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            OkActivityResult.instance.startActivityForResult(
                context,
                (apkInstallPermissionIntentGenerator ?: ApkInstallPermissionIntentGenerator()).generatorIntent(context)
            ) { _, _ ->
                if (context.packageManager.canRequestPackageInstalls()) {
                    onResult(emptyArray())
                } else {
                    onResult(permissions)
                }
            }
        } else {
            onResult(emptyArray())
        }
    }
}