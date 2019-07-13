package com.kelin.okpermission.router

import android.content.Intent

/**
 * **描述:** startActivityForResult的路由。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-06-30  10:05
 *
 * **版本:** v 1.0.0
 */
interface ActivityResultRouter {

    fun startActivityForResult(intent: Intent, onResult: (resultCode: Int, data: Intent) -> Unit)
}