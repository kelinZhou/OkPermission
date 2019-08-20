package com.kelin.okpermission.intentgenerator

import android.content.ComponentName
import android.content.Context
import android.content.Intent

/**
 * **描述:** 华为&荣耀的Application详情页的意图构建器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:33
 *
 * **版本:** v 1.0.0
 */
class EMUISettingsIntentGenerator : SettingIntentGenerator {
    override fun generatorIntent(context: Context): Intent {
        return Intent(context.packageName).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            component = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")
        }
    }
}