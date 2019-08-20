package com.kelin.okpermission.intentgenerator

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * **描述:** 默认的Application详情页的意图构建器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:33
 *
 * **版本:** v 1.0.0
 */
class AppDetailIntentGenerator : SettingIntentGenerator {
    override fun generatorIntent(context: Context): Intent {
        return Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + context.packageName)
        )
    }
}