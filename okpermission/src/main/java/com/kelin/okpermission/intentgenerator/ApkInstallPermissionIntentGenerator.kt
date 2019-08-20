package com.kelin.okpermission.intentgenerator

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi

/**
 * **描述:** 跳转到应用安装权限申请的意图构建器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:33
 *
 * **版本:** v 1.0.0
 */
@RequiresApi(Build.VERSION_CODES.O)
class ApkInstallPermissionIntentGenerator : SettingIntentGenerator {
    override fun generatorIntent(context: Context): Intent {
        return  Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
    }
}