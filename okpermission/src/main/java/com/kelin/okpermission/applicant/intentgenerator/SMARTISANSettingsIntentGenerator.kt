package com.kelin.okpermission.applicant.intentgenerator

import android.content.Context
import android.content.Intent
import android.content.ComponentName
import android.net.Uri
import android.os.Build
import com.kelin.okpermission.BuildConfig
import com.kelin.okpermission.permission.Permission


/**
 * **描述:** 三星手机的Application详情页的意图构建器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:33
 *
 * **版本:** v 1.0.0
 */
class SMARTISANSettingsIntentGenerator(permission: Permission?) : SettingIntentGenerator(permission) {
    override fun onGeneratorDangerousIntent(context: Context): Intent {
        return generatorAppDetailIntent(context)
    }

    override fun onGeneratorSystemWindowIntent(context: Context): Intent {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            onGeneratorDangerousIntent(context)
        }else{
            super.onGeneratorSystemWindowIntent(context)
        }
    }
}