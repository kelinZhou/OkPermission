package com.kelin.okpermission.intentgenerator

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.NotificationManagerCompat
import com.kelin.okpermission.OkPermission
import com.kelin.okpermission.permission.Permission

/**
 * **描述:** 权限设计页面跳转器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:26
 *
 * **版本:** v 1.0.0
 */
abstract class SettingIntentGenerator {
    abstract val permission: Permission?

    fun generatorIntent(context: Context): Intent {
        val p = permission?.permission
        return if (p == OkPermission.permission.NOTIFICATION) {
            onGeneratorNotificationIntent(context)
        } else if (p == Manifest.permission.REQUEST_INSTALL_PACKAGES && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
        } else {
            onGeneratorDangerousIntent(context)
        }
    }

    open fun onGeneratorNotificationIntent(context: Context, permission:Permission? = this.permission): Intent {
        val intent = Intent()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val channel = if (permission != null && permission is Permission.NotificationPermission && NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                    permission.channel
                } else {
                    ""
                }
                intent.action = if (channel.isNotEmpty()) {
                    Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                } else {
                    Settings.ACTION_APP_NOTIFICATION_SETTINGS
                }
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                if (channel.isNotEmpty()) {
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel)
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {  //5.0
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("app_package", context.packageName)
                intent.putExtra("app_uid", context.applicationInfo.uid)
            }
            Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT -> {  //4.4
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("package:" + context.packageName)
            }
            Build.VERSION.SDK_INT >= 15 -> {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.parse("package:" + context.packageName)
            }
            else -> {
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                Uri.parse("package:" + context.packageName)
            }
        }
        return intent
    }

    abstract fun onGeneratorDangerousIntent(context: Context): Intent


    fun generatorAppDetailIntent(context: Context): Intent {
        return Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + context.packageName)
        )
    }

    companion object {
        fun checkIntentAvailable(context: Context, intent: Intent): Boolean {
            return context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()
        }
    }
}