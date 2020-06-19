package com.kelin.okpermission.applicant.intentgenerator

import android.content.Context
import android.content.Intent
import android.content.ComponentName
import com.kelin.okpermission.permission.Permission


/**
 * **描述:** 魅族的Application详情页的意图构建器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:33
 *
 * **版本:** v 1.0.0
 */
class MeiZuSettingsIntentGenerator(permission: Permission?) : SettingIntentGenerator(permission) {
    override fun onGeneratorDangerousIntent(context: Context): Intent {
        val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
        intent.putExtra("packageName", context.packageName)
        intent.component = ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity")
        return intent
    }
}