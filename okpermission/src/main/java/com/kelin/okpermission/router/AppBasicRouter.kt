package com.kelin.okpermission.router

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlin.random.Random

/**
 * **描述:** 路由的基类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-06-30  10:33
 *
 * **版本:** v 1.0.0
 */
abstract class AppBasicRouter : Fragment() {

    protected val randomGenerator = Random(System.currentTimeMillis())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
}