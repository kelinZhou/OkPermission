package com.kelin.okpermission.applicant

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.kelin.okpermission.permission.Permission
import com.kelin.okpermission.router.PermissionRequestRouter

/**
 * **描述:** 默认的权限申请器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-15  16:30
 *
 * **版本:** v 1.0.0
 */
class DefaultApplicant(context: Activity) : PermissionsApplicant(context) {

    override fun checkSelfPermission(permission: Permission): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            permission.permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun shouldShowRequestPermissionRationale(
        router: PermissionRequestRouter,
        permission: Permission
    ): Boolean {
        return router.shouldShowRequestPermissionRationale(permission.permission)
    }

    override fun requestPermissions(
        router: PermissionRequestRouter,
        permissions: Array<out Permission>,
        onResult: (permissions: Array<out Permission>) -> Unit
    ) {
        router.requestPermissions(permissions, onResult)
    }
}