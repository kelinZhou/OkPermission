package com.kelin.okpermission.applicant.intentgenerator

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.kelin.okpermission.permission.Permission

/**
 * **描述:** 华为&荣耀的Application详情页的意图构建器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:33
 *
 * **版本:** v 1.0.0
 */
class EMUISettingsIntentGenerator(permission: Permission?) : SettingIntentGenerator(permission) {
    override fun onGeneratorDangerousIntent(context: Context): Intent {
        val intent = Intent()

        intent.setClassName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.SingleAppActivity")
        if (checkIntentAvailable(context, intent)) {
            return intent
        }

        intent.component = ComponentName(
            "com.android.packageinstaller",
            "com.android.packageinstaller.permission.ui.ManagePermissionsActivity"
        )
        if (checkIntentAvailable(context, intent)) {
            return intent
        }

        intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")
        return if (checkIntentAvailable(context, intent)) {
            intent
        } else intent
    }

    override fun onGeneratorNotificationIntent(context: Context, permission: Permission?): Intent {
        return super.onGeneratorNotificationIntent(context, permission)
    }
}