package com.kelin.okpermission.applicant.intentgenerator

import android.content.Context
import android.content.Intent
import android.content.ComponentName
import com.kelin.okpermission.permission.Permission


/**
 * **描述:** OPPO的Application详情页的意图构建器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:33
 *
 * **版本:** v 1.0.0
 */
class OPPOSettingsIntentGenerator(permission: Permission?) : SettingIntentGenerator(permission) {
    override fun onGeneratorDangerousIntent(context: Context): Intent {
        val intent = Intent()
        intent.putExtra("packagename", context.packageName)

        // vivo x7 Y67 Y85
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity")
        if (SettingIntentGenerator.checkIntentAvailable(context, intent)) {
            return intent
        }

        // vivo Y66 x20 x9
        intent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity")
        if (SettingIntentGenerator.checkIntentAvailable(context, intent)) {
            return intent
        }

        // Y85
        intent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.PurviewTabActivity")
        if (SettingIntentGenerator.checkIntentAvailable(context, intent)) {
            return intent
        }

        // 跳转会报 java.lang.SecurityException: Permission Denial
        intent.setClassName("com.android.packageinstaller", "com.android.packageinstaller.permission.ui.ManagePermissionsActivity")
        if (SettingIntentGenerator.checkIntentAvailable(context, intent)) {
            return intent
        }

        intent.component = ComponentName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity")
        return intent
    }
}