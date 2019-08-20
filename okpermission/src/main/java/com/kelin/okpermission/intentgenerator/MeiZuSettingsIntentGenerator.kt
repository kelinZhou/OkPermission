package com.kelin.okpermission.intentgenerator

import android.content.Context
import android.content.Intent

/**
 * **描述:** 魅族的Application详情页的意图构建器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:33
 *
 * **版本:** v 1.0.0
 */
class MeiZuSettingsIntentGenerator : SettingIntentGenerator {
    override fun generatorIntent(context: Context): Intent {
        return Intent("com.meizu.safe.security.SHOW_APPSEC").apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            putExtra("packageName", context.packageName)
        }
    }
}