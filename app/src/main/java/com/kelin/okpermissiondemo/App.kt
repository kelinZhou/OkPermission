package com.kelin.okpermissiondemo

import android.app.Application
import com.kelin.okpermission.OkPermission

/**
 * **描述:** 自定义Application.
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-07-13  14:04
 *
 * **版本:** v 1.0.0
 */
class App : Application(){
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.init(this)
    }
}