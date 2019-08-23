package com.kelin.okpermission.applicant

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationManagerCompat
import com.kelin.okpermission.OkActivityResult
import com.kelin.okpermission.permission.Permission
import com.kelin.okpermission.router.PermissionRequestRouter

/**
 * **描述:** 通知权限申请器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-21  13:49
 *
 * **版本:** v 1.0.0
 */
class NotificationApplicant(activity: Activity) : PermissionsApplicant(activity) {

    override fun checkSelfPermissions(permission: Permission): Boolean {
        return NotificationManagerCompat.from(applicationContext).areNotificationsEnabled() && if (permission is Permission.NotificationPermission) {
            isNotificationChannelEnabled(permission.channel)
        } else {
            true
        }
    }

    private fun isNotificationChannelEnabled(channel: String): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.getNotificationChannel(channel)?.importance != NotificationManager.IMPORTANCE_NONE
            }
            else -> true
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
        val channels = permissions.filter { it is Permission.NotificationPermission && it.channel.isNotEmpty() }.map {
            it as Permission.NotificationPermission
        }
        OkActivityResult.instance.startActivityForResult(
            activity,
            intentGenerator.onGeneratorNotificationIntent(
                activity,
                if (channels.size == 1) {
                    channels[0]
                } else {
                    null
                }
            )
        ) { _, _, e ->
            if (e == null) {
                if (NotificationManagerCompat.from(activity).areNotificationsEnabled() && channels.all { isNotificationChannelEnabled(it.channel) }) {
                    onResult(emptyArray())
                } else {
                    onResult(permissions)
                }
            } else {
                applyTryAgain(onResult, permissions)
            }
        }

    }

    private fun applyTryAgain(
        onResult: (permissions: Array<out Permission>) -> Unit,
        permissions: Array<out Permission>
    ) {
        OkActivityResult.instance.startActivityForResult(
            activity,
            intentGenerator.generatorAppDetailIntent(activity)
        ) { _, _, exception ->
            if (exception == null) {
                if (NotificationManagerCompat.from(activity).areNotificationsEnabled()) {
                    onResult(emptyArray())
                } else {
                    onResult(permissions)
                }
            } else {
                onResult(permissions)
            }
        }
    }
}