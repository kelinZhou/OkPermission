package com.kelin.okpermission

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.SparseArray
import com.kelin.okpermission.router.ActivityResultRouter
import com.kelin.okpermission.router.BasicRouter
import com.kelin.okpermission.router.SupportBasicRouter

/**
 * **描述:** startActivityForResult的帮助工具。。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-06-29  13:18
 *
 * **版本:** v 1.0.0
 */
class OkActivityResult private constructor() {

    companion object {
        private const val ROUTER_TAG = "ok_permission_activity_result_router_tag"

        val instance: OkActivityResult by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { OkActivityResult() }
    }

    fun startActivityForResult(context: Activity, clazz: Class<out Activity>, onResult: (resultCode: Int, data: Intent) -> Unit) {
        startActivityForResult(context, Intent(context, clazz), onResult)
    }

    fun startActivityForResult(context: Activity, intent: Intent, onResult: (resultCode: Int, data: Intent) -> Unit) {
        getRouter(context).startActivityForResult(intent, onResult)
    }

    private fun getRouter(activity: Activity): ActivityResultRouter {
        return findRouter(activity) ?: createRouter(activity)
    }

    private fun createRouter(activity: Activity): ActivityResultRouter {
        val router: ActivityResultRouter
        if (activity is FragmentActivity) {
            router = SupportActivityResultRouterImpl()
            val fm = activity.supportFragmentManager
            fm.beginTransaction()
                .add(router, ROUTER_TAG)
                .commitAllowingStateLoss()
            fm.executePendingTransactions()
        } else {
            router = ActivityResultRouterImpl()
            val fm = activity.fragmentManager
            fm.beginTransaction()
                .add(router, ROUTER_TAG)
                .commitAllowingStateLoss()
            fm.executePendingTransactions()
        }
        return router
    }

    private fun findRouter(activity: Activity): ActivityResultRouter? {
        return if (activity is FragmentActivity) {
            activity.supportFragmentManager.findFragmentByTag(ROUTER_TAG) as? ActivityResultRouter
        } else {
            activity.fragmentManager.findFragmentByTag(ROUTER_TAG) as? ActivityResultRouter
        }
    }

    internal class ActivityResultRouterImpl : BasicRouter(), ActivityResultRouter {

        private val resultCallbackCache = SparseArray<(resultCode: Int, data: Intent) -> Unit>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        override fun startActivityForResult(intent: Intent, onResult: (resultCode: Int, data: Intent) -> Unit) {
            val requestCode = makeRequestCode()
            resultCallbackCache.put(requestCode, onResult)
            startActivityForResult(intent, requestCode)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            val callback = resultCallbackCache[requestCode]
            resultCallbackCache.remove(requestCode)
            callback?.invoke(resultCode, data ?: Intent())
        }

        override fun onDestroy() {
            super.onDestroy()
            resultCallbackCache.clear()
        }

        /**
         * 生成一个code。
         */
        private fun makeRequestCode(): Int {
            val code = randomGenerator.nextInt(0, 0x0001_0000)
            return if (resultCallbackCache.indexOfKey(code) < 0) {
                code
            } else {
                makeRequestCode()
            }
        }
    }

    internal class SupportActivityResultRouterImpl : SupportBasicRouter(), ActivityResultRouter {

        private val resultCallbackCache = SparseArray<(resultCode: Int, data: Intent) -> Unit>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        override fun startActivityForResult(intent: Intent, onResult: (resultCode: Int, data: Intent) -> Unit) {
            val requestCode = makeRequestCode()
            resultCallbackCache.put(requestCode, onResult)
            startActivityForResult(intent, requestCode)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            val callback = resultCallbackCache[requestCode]
            resultCallbackCache.remove(requestCode)
            callback?.invoke(resultCode, data ?: Intent())
        }

        override fun onDestroy() {
            super.onDestroy()
            resultCallbackCache.clear()
        }

        /**
         * 生成一个code。
         */
        private fun makeRequestCode(): Int {
            val code = randomGenerator.nextInt(0, 0x0001_0000)
            return if (resultCallbackCache.indexOfKey(code) < 0) {
                code
            } else {
                makeRequestCode()
            }
        }
    }
}