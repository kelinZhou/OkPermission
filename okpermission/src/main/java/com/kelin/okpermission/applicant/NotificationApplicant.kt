package com.kelin.okpermission.applicant

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.kelin.okpermission.OkActivityResult
import com.kelin.okpermission.permission.Permission
import com.kelin.okpermission.permission.PermissionsCallback
import com.kelin.okpermission.router.PermissionRouter

/**
 * **描述:** 通知权限申请器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-21  13:49
 *
 * **版本:** v 1.0.0
 */
class NotificationApplicant(activity: Activity, router: PermissionRouter) : PermissionsApplicant(activity, router) {

    override fun checkSelfPermission(permission: Permission): Boolean {
        return areNotificationsEnabled() && if (permission is Permission.NotificationPermission) {
            isNotificationChannelEnabled(permission.channel)
        } else {
            true
        }
    }

    private fun areNotificationsEnabled() = NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()

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
        router: PermissionRouter,
        permission: Permission
    ): Boolean {
        return true
    }

    override fun requestPermissions(
        router: PermissionRouter,
        permissions: Array<out Permission>,
        onResult: PermissionsCallback
    ) {
        if (areNotificationsEnabled()) {
            onRequestChannels(permissions, router, onResult)
        } else {
            OkActivityResult.startActivityForCodeOrException(
                activity,
                intentGenerator.generatorIntent(activity)
            ) { _, e ->
                if (e == null) {
                    if (areNotificationsEnabled()) { //如果总开关开了就接着判断渠道
                        onRequestChannels(permissions, router, onResult)
                    } else { //如果总开关没开申请渠道没有意义，所以直接回调失败。
                        onResult(permissions)
                    }
                } else {
                    applyTryAgain(onResult, permissions)
                }
            }
        }
    }

    private fun onRequestChannels(
        permissions: Array<out Permission>,
        router: PermissionRouter,
        onResult: PermissionsCallback
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = permissions.filter {
                it is Permission.NotificationPermission && it.channel.isNotEmpty()
            }.map {
                it as Permission.NotificationPermission
            }.toTypedArray()
            if (channels.isNotEmpty()) {
                if (channels.all { isNotificationChannelEnabled(it.channel) }) {
                    onResult(emptyArray())
                } else {
                    doOnRequestChannelsNotification(router, channels, ArrayList(), 0, onResult)
                }
            } else {
                OkActivityResult.startActivityForCodeOrException(
                    activity,
                    intentGenerator.generatorIntent(activity)
                ) { _, e ->
                    if (e == null) {
                        if (areNotificationsEnabled()) { //既然没有渠道就判断总开关就好了。
                            onResult(emptyArray())
                        } else {
                            onResult(permissions)
                        }
                    } else {
                        applyTryAgain(onResult, permissions)
                    }
                }
            }
        } else {
            onResult(emptyArray())
        }
    }

    private fun doOnRequestChannelsNotification(
        router: PermissionRouter,
        permissions: Array<out Permission.NotificationPermission>,
        deniedPermissions: MutableList<Permission.NotificationPermission>,
        index: Int,
        onResult: PermissionsCallback
    ) {
        val curPermission = permissions[index]
        OkActivityResult.startActivityForCodeOrException(
            activity,
            intentGenerator.generatorIntent(
                activity,
                curPermission
            )
        ) { _, e ->
            if (e == null) {
                if (!isNotificationChannelEnabled(curPermission.channel)) {
                    deniedPermissions.add(curPermission)
                }
                if (index < permissions.lastIndex) {
                    doOnRequestChannelsNotification(router, permissions, deniedPermissions, index + 1, onResult)
                } else {
                    onResult(deniedPermissions.toTypedArray())
                }
            } else {
                applyTryAgain(onResult, permissions)
            }
        }
    }

    private fun applyTryAgain(
        onResult: PermissionsCallback,
        permissions: Array<out Permission>
    ) {
        OkActivityResult.startActivityForCodeOrException(
            activity,
            intentGenerator.generatorAppDetailIntent(activity)
        ) { _, exception ->
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