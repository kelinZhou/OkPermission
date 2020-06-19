package com.kelin.okpermission.router

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import kotlin.random.Random

/**
 * **描述:** Support路由的基类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-06-30  10:33
 *
 * **版本:** v 1.0.0
 */
abstract class SupportBasicRouter : Fragment() {

    protected val emptyIntent : Intent by lazy { Intent() }

    protected val randomGenerator = Random(System.currentTimeMillis())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
}