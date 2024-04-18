package com.kelin.okpermission.applicant

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.kelin.okpermission.permission.Permission
import com.kelin.okpermission.permission.PermissionsCallback
import com.kelin.okpermission.router.PermissionRouter

/**
 * **描述:** 默认的权限申请器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-15  16:30
 *
 * **版本:** v 1.0.0
 */
class DefaultApplicant(activity: Activity, router: PermissionRouter) : PermissionsApplicant(activity, router) {

    override fun checkSelfPermission(permission: Permission): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            permission.permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun shouldShowRequestPermissionRationale(
        router: PermissionRouter,
        permission: Permission
    ): Boolean {
        return router.shouldShowRequestPermissionRationale(permission.permission)
    }

    override fun requestPermissions(
        router: PermissionRouter,
        permissions: Array<out Permission>,
        onResult: PermissionsCallback
    ) {
        router.requestPermissions(permissions, onResult)
    }
}