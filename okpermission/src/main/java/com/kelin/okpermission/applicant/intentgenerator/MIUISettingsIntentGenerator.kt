package com.kelin.okpermission.applicant.intentgenerator

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.kelin.okpermission.permission.Permission

/**
 * **描述:** 小米的Application详情页的意图构建器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:33
 *
 * **版本:** v 1.0.0
 */
class MIUISettingsIntentGenerator(permission: Permission?) : SettingIntentGenerator(permission) {
    override fun onGeneratorDangerousIntent(context: Context): Intent {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.putExtra("extra_pkgname", context.packageName)
        if (checkIntentAvailable(context, intent)) {
            return intent
        }

        intent.setPackage("com.miui.securitycenter")
        if (checkIntentAvailable(context, intent)) {
            return intent
        }

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
        if (checkIntentAvailable(context, intent)) {
            return intent
        }

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
        return intent
    }

    override fun onGeneratorSystemWindowIntent(context: Context): Intent {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            when (val version = getMIUIVersion()) {
                5 -> {
                    val packageName = context.packageName
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent
                }
                6 -> {
                    val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                    intent.setClassName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
                    )
                    intent.putExtra("extra_pkgname", context.packageName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent
                }
                7 -> {
                    val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                    intent.setClassName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
                    )
                    intent.putExtra("extra_pkgname", context.packageName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent
                }
                8 -> {
                    var intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                    intent.setClassName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.permissions.PermissionsEditorActivity"
                    )
//        intent.setPackage("com.miui.securitycenter");
                    intent.putExtra("extra_pkgname", context.packageName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    if (!checkIntentAvailable(context, intent)) {
                        intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                        intent.setPackage("com.miui.securitycenter")
                        intent.putExtra("extra_pkgname", context.packageName)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    intent
                }
                else -> {
                    Log.e(
                        "====",
                        "MIUISettingsIntentGenerator: this is a special MIUI rom version, its version code $version"
                    )
                    generatorAppDetailIntent(context)
                }
            }
        } else {
            super.onGeneratorSystemWindowIntent(context)
        }
    }

    private fun getMIUIVersion(): Int {
        val version = getSystemProperty("ro.miui.ui.version.name")
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1))
            } catch (e: Exception) {
                Log.getStackTraceString(e)
            }

        }
        return -1
    }
}