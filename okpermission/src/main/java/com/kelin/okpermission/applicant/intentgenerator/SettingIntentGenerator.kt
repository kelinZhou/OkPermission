package com.kelin.okpermission.applicant.intentgenerator

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.kelin.okpermission.OkPermission
import com.kelin.okpermission.permission.Permission
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * **描述:** 权限设计页面跳转器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:26
 *
 * **版本:** v 1.0.0
 */
abstract class SettingIntentGenerator(private val permission: Permission?) {

    fun generatorIntent(context: Context, permission: Permission? = this.permission): Intent {
        val target = permission ?: this.permission
        val p = target?.permission
        return if (p == Manifest.permission.SYSTEM_ALERT_WINDOW) {
            onGeneratorSystemWindowIntent(context)
        } else if (p == OkPermission.permission.NOTIFICATION) {
            onGeneratorNotificationIntent(context, target)
        } else if (p == Manifest.permission.REQUEST_INSTALL_PACKAGES && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            onGeneratorApkInstallIntent(context)
        } else if (p == Manifest.permission.WRITE_SETTINGS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onGeneratorWriteSettingsIntent(context)
        } else {
            onGeneratorDangerousIntent(context)
        }
    }

    protected open fun onGeneratorSystemWindowIntent(context: Context): Intent {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->  //8.0以上
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    data = Uri.parse("package:" + context.packageName)
                }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> //6.0以上
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    data = Uri.parse("package:" + context.packageName)
                }
            else -> generatorAppDetailIntent(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    protected open fun onGeneratorApkInstallIntent(context: Context): Intent {
        return Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    protected open fun onGeneratorWriteSettingsIntent(context: Context): Intent {
        return Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:${context.packageName}"))
    }

    protected open fun onGeneratorNotificationIntent(
        context: Context,
        permission: Permission? = this.permission
    ): Intent {
        val intent = Intent()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val channel =
                    if (permission != null && permission is Permission.NotificationPermission && NotificationManagerCompat.from(
                            context
                        ).areNotificationsEnabled()
                    ) {
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

    protected abstract fun onGeneratorDangerousIntent(context: Context): Intent


    fun generatorAppDetailIntent(context: Context): Intent {
        return Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + context.packageName)
        )
    }

    protected fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return line
    }

    companion object {
        fun checkIntentAvailable(context: Context, intent: Intent): Boolean {
            return context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()
        }
    }
}