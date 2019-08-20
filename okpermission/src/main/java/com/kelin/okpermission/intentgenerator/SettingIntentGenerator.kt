package com.kelin.okpermission.intentgenerator

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

/**
 * **描述:** 权限设计页面跳转器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:26
 *
 * **版本:** v 1.0.0
 */
interface SettingIntentGenerator {
    fun generatorIntent(context: Context): Intent

    companion object{
        fun checkIntentAvailable(context: Context, intent: Intent): Boolean {
            return context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()
        }
    }
}