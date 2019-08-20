package com.kelin.okpermission.intentgenerator

import android.content.Context
import android.content.Intent
import android.content.ComponentName
import com.kelin.okpermission.BuildConfig


/**
 * **描述:** 360手机的Application详情页的意图构建器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:33
 *
 * **版本:** v 1.0.0
 */
class Safe360SettingsIntentGenerator : SettingIntentGenerator {
    override fun generatorIntent(context: Context): Intent {
        val intent = Intent("android.intent.action.MAIN")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
        intent.component = ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity")
        return intent
    }
}