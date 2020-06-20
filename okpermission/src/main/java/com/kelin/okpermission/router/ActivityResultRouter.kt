package com.kelin.okpermission.router

import android.content.Intent
import android.os.Bundle

/**
 * **描述:** startActivityForResult的路由。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-06-30  10:05
 *
 * **版本:** v 1.0.0
 */
interface ActivityResultRouter<D> {

    fun startActivityForResult(intent: Intent, options:Bundle? = null, onResult: (resultCode: Int, data: D?, e: Exception?) -> Unit)
}