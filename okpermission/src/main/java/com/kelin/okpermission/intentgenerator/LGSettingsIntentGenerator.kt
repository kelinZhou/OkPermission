package com.kelin.okpermission.intentgenerator

import android.content.Context
import android.content.Intent
import android.content.ComponentName
import com.kelin.okpermission.BuildConfig
import com.kelin.okpermission.permission.Permission


/**
 * **描述:** LG的Application详情页的意图构建器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:33
 *
 * **版本:** v 1.0.0
 */
class LGSettingsIntentGenerator(override val permission: Permission?) : SettingIntentGenerator() {
    override fun onGeneratorDangerousIntent(context: Context): Intent {
        val intent = Intent("android.intent.action.MAIN")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
        intent.component = ComponentName("com.android.settings", "com.android.settings.Settings\$AccessLockSummaryActivity")
        return intent
    }
}