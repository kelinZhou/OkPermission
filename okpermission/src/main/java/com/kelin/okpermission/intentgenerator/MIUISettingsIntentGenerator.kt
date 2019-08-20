package com.kelin.okpermission.intentgenerator

import android.content.Context
import android.content.Intent

/**
 * **描述:** 小米的Application详情页的意图构建器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  17:33
 *
 * **版本:** v 1.0.0
 */
class MIUISettingsIntentGenerator : SettingIntentGenerator {
    override fun generatorIntent(context: Context): Intent {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.putExtra("extra_pkgname", context.packageName)
        if (SettingIntentGenerator.checkIntentAvailable(context, intent)) {
            return intent
        }

        intent.setPackage("com.miui.securitycenter")
        if (SettingIntentGenerator.checkIntentAvailable(context, intent)) {
            return intent
        }

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
        if (SettingIntentGenerator.checkIntentAvailable(context, intent)) {
            return intent
        }

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
        return intent
    }
}