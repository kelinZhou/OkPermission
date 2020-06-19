package com.kelin.okpermission.applicant

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.os.Binder
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import com.kelin.okpermission.OkActivityResult
import com.kelin.okpermission.permission.Permission
import com.kelin.okpermission.router.PermissionRequestRouter
import java.lang.reflect.Method

/**
 * **描述:** 系统悬浮窗权限的申请器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-23  13:30
 *
 * **版本:** v 1.0.0
 */
class SystemWindowApplicant(activity: Activity) : PermissionsApplicant(activity) {

    override fun checkSelfPermission(permission: Permission): Boolean {
        return checkSystemPermission()
    }

    private fun checkSystemPermission(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> //6.0以上
                Settings.canDrawOverlays(activity)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> getAppOps() //4.4-5.1
            else -> true //4.4以下

        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun getAppOps(): Boolean {
        val any = activity.applicationContext.getSystemService(Context.APP_OPS_SERVICE)
        return if (any != null) {
            try {
                any.javaClass
                    .getMethod("checkOp", Int::class.java, Int::class.java, String::class.java)
                    .invoke(
                        any, 24, Binder.getCallingUid(), activity.packageName
                    ) == AppOpsManager.MODE_ALLOWED
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } else {
            false
        }
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
        OkActivityResult.startActivityOrException(
            activity,
            intentGenerator.generatorIntent(activity)
        ) { _, _, e ->
            if (checkSystemPermission()) {
                onResult(emptyArray())
            } else {
                onResult(permissions)
            }
        }
    }
}