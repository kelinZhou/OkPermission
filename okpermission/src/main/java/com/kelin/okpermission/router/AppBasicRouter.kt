package com.kelin.okpermission.router

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * **描述:** 路由的基类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-06-30  10:33
 *
 * **版本:** v 1.0.0
 */
abstract class AppBasicRouter : Fragment(){
    protected var isCreated = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCreated = true
    }

    override fun onDestroy() {
        super.onDestroy()
        isCreated = false
    }
}