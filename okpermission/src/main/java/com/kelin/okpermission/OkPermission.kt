package com.kelin.okpermission

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.StringRes
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.SparseArray
import com.kelin.okpermission.router.BasicRouter
import com.kelin.okpermission.router.PermissionRequestRouter
import com.kelin.okpermission.router.SupportBasicRouter
import java.util.*

/**
 * **描述:** 权限申请的核心类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-06-29  20:06
 *
 * **版本:** v 1.0.0
 */
class OkPermission {
    companion object {
        private const val ROUTER_TAG = "ok_permission_apply_permissions_router_tag"

        private val activityStack = ArrayList<Activity>()
        fun init(application: Application) {
            application.registerActivityLifecycleCallbacks(ActivityLifecycleCallback())
        }

        val instance: OkPermission by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { OkPermission() }
    }

    private val topActivity: Activity?
        get() = if (activityStack.isEmpty()) null else activityStack.last()


    /**
     * **申请权限。**
     *
     * 当需要申请系统权限的时候调用，调用者无需关心要申请的权限是否已经被用户授予。只需无脑调用该方法即可。
     * 该方法会自动检测权限是否已经被授予，如果没有被授予这回去申请权限，如果已经被授予则会直接执行回调。
     *
     * @param permissions 要申请的权限。
     * @param listener 申请结果的监听。如果回调中的permissions为空则表示所有权限已经被获取，
     * 否则就表示用户拒绝了某个或某些权限，而被拒绝的权限就是permissions集合中的权限。
     */

    fun applyPermissions(vararg permissions: String, listener: (permissions: Array<out String>) -> Unit) {
        checkPermission(false, PermissionWrapper(permissions, null), listener)
    }

    fun applyPermissions(@StringRes explain: Int, vararg permissions: String, listener: (permissions: Array<out String>) -> Unit) {
        checkPermission(false, PermissionWrapper(permissions, getString(explain)), listener)
    }

    fun forceApplyPermissions(vararg permissions: String, listener: (permissions: Array<out String>) -> Unit) {
        checkPermission(true, PermissionWrapper(permissions, null), listener)
    }

    fun forceApplyPermissions(@StringRes explain: Int, vararg permissions: String, listener: (permissions: Array<out String>) -> Unit) {
        checkPermission(true, PermissionWrapper(permissions, getString(explain)), listener)
    }

    private fun getString(@StringRes text: Int): String? {
        return if (text == 0) null else topActivity?.getString(text)
    }

    /**
     * 检测权限授予情况。如果所有权限已被授予则直接回调，否则就去尝试获取权限。
     *
     * @param force 本次要获取的权限是否是必须的权限，如果是必须的权限则会强引导用户授予权限。否则，则是若引导用户授予权限。
     * @param permissions 要申请的权限。
     * @param listener 申请结果的监听。如果回调中的permissions为空则表示所有权限已经被获取，
     * 否则就表示用户拒绝了某个或某些权限，而被拒绝的权限就是permissions集合中的权限。
     */
    private fun checkPermission(force: Boolean, permissions: PermissionWrapper, listener: (permissions: Array<out String>) -> Unit) {
        val activity = topActivity
        if (activity != null) {
            val deniedPermissions = ArrayList<String>()
            permissions.permissions.forEach { permission ->
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission)
                }
            }
            if (deniedPermissions.isEmpty()) {
                listener(emptyArray())
            } else {
                requestPermissions(force, PermissionWrapper(deniedPermissions.toTypedArray(), permissions.explain), listener)
            }
        } else {
            listener(permissions.permissions)
        }
    }

    private fun requestPermissions(force: Boolean, deniedPermissions: PermissionWrapper, listener: (permissions: Array<out String>) -> Unit) {
        val activity = topActivity
        if (activity != null) {
            val router = getRouter(activity)
            router.requestPermissions(deniedPermissions.permissions) { permissions, grantResults ->
                val hadAllPermission = !(grantResults.any { it != PackageManager.PERMISSION_GRANTED })
                if (hadAllPermission) {
                    listener(emptyArray())
                } else {
                    if (permissions.any { router.shouldShowRequestPermissionRationale(it) }) {
                        if (!deniedPermissions.explain.isNullOrEmpty()) {
                            showRequestPermissionsExplain(force, deniedPermissions.explain) { isContinue ->
                                handlerPermissionsDenied(isContinue, force, permissions, deniedPermissions, listener)
                            }
                        } else {
                            handlerPermissionsDenied(force, force, permissions, deniedPermissions, listener)
                        }
                    } else {
                        if (!deniedPermissions.explain.isNullOrEmpty()) {
                            showRequestPermissionsExplain(force, deniedPermissions.explain) { isContinue ->
                                if (isContinue) {
                                    showMissingPermissionDialog(force, PermissionWrapper(permissions, deniedPermissions.explain), listener)
                                } else {
                                    listener(permissions)
                                }
                            }
                        } else {
                            showMissingPermissionDialog(force, PermissionWrapper(permissions, deniedPermissions.explain), listener)
                        }
                    }
                }
            }
        }
    }

    private fun handlerPermissionsDenied(isContinue: Boolean, force: Boolean, permissions: Array<String>, deniedPermissions: PermissionWrapper, listener: (permissions: Array<out String>) -> Unit) {
        if (isContinue) {
            requestPermissions(force, PermissionWrapper(permissions, deniedPermissions.explain), listener)
        } else {
            listener(permissions)
        }
    }

    private fun showRequestPermissionsExplain(force: Boolean, explain: String, listener: (isContinue: Boolean) -> Unit) {
        val activity = topActivity
        if (activity != null) {
            AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle("提示")
                .setMessage(explain)
                .apply {
                    if (!force) {
                        setNegativeButton("取消") { _, _ -> listener(false) }
                    }
                }
                .setPositiveButton("继续") { _, _ -> listener(true) }
                .show()
        }
    }

    private fun showMissingPermissionDialog(force: Boolean, deniedPermissions: PermissionWrapper, listener: (permissions: Array<out String>) -> Unit) {
        val activity = topActivity
        if (activity != null) {
            AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle("帮助")
                .setMessage("当前操作缺少必要权限。\n请点击\"设置\"-\"权限\"-打开所需权限。\n最后点击两次后退按钮，即可返回。")
                .apply {
                    if (!force) {
                        setNegativeButton("退出") { _, _ -> listener(deniedPermissions.permissions) }
                    }
                }
                .setPositiveButton("设置") { _, _ ->
                    OkActivityResult.instance.startActivityForResult(activity, Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.packageName))) { _, _ ->
                        requestPermissions(force, deniedPermissions, listener)
                    }
                }.show()
        }
    }

    private fun getRouter(context: Activity): PermissionRequestRouter {
        return findRouter(context) ?: createRouter(context)
    }

    private fun createRouter(activity: Activity): PermissionRequestRouter {
        val router: PermissionRequestRouter
        if (activity is FragmentActivity) {
            router = SupportPermissionRouter()
            val fm = activity.supportFragmentManager
            fm.beginTransaction()
                .add(router, ROUTER_TAG)
                .commitAllowingStateLoss()
            fm.executePendingTransactions()
        } else {
            router = PermissionRouter()
            val fm = activity.fragmentManager
            fm.beginTransaction()
                .add(router, ROUTER_TAG)
                .commitAllowingStateLoss()
            fm.executePendingTransactions()
        }
        return router
    }

    private fun findRouter(activity: Activity): PermissionRequestRouter? {
        return if (activity is FragmentActivity) {
            activity.supportFragmentManager.findFragmentByTag(ROUTER_TAG) as? PermissionRequestRouter
        } else {
            activity.fragmentManager.findFragmentByTag(ROUTER_TAG) as? PermissionRequestRouter
        }
    }

    private class ActivityLifecycleCallback : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            activityStack.add(activity)
        }

        override fun onActivityDestroyed(activity: Activity) {
            activityStack.remove(activity)
        }

        override fun onActivityPaused(activity: Activity?) {}

        override fun onActivityResumed(activity: Activity?) {}

        override fun onActivityStarted(activity: Activity?) {}

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

        override fun onActivityStopped(activity: Activity?) {}
    }

    internal class PermissionRouter : BasicRouter(), PermissionRequestRouter {
        private val permissionCallbackCache = SparseArray<(permissions: Array<String>, grantResults: IntArray) -> Unit>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        override fun requestPermissions(permissions: Array<out String>, onResult: (permissions: Array<String>, grantResults: IntArray) -> Unit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val requestCode = makeRequestCode()
                permissionCallbackCache.put(requestCode, onResult)
                requestPermissions(permissions, requestCode)
            } else {
                onResult(emptyArray(), IntArray(0))
            }
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            val callback = permissionCallbackCache[requestCode]
            permissionCallbackCache.remove(requestCode)
            callback?.invoke(permissions, grantResults)
        }

        override fun onDestroy() {
            super.onDestroy()
            permissionCallbackCache.clear()
        }

        /**
         * 生成一个code。
         */
        private fun makeRequestCode(): Int {
            val code = randomGenerator.nextInt(0, 0x0001_0000)
            return if (permissionCallbackCache.indexOfKey(code) < 0) {
                code
            } else {
                makeRequestCode()
            }
        }
    }

    internal class SupportPermissionRouter : SupportBasicRouter(), PermissionRequestRouter {

        private val permissionCallbackCache = SparseArray<(permissions: Array<String>, grantResults: IntArray) -> Unit>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        override fun requestPermissions(permissions: Array<out String>, onResult: (permissions: Array<String>, grantResults: IntArray) -> Unit) {
            val requestCode = makeRequestCode()
            permissionCallbackCache.put(requestCode, onResult)
            requestPermissions(permissions, requestCode)
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            val callback = permissionCallbackCache[requestCode]
            permissionCallbackCache.remove(requestCode)
            callback?.invoke(permissions, grantResults)
        }

        override fun onDestroy() {
            super.onDestroy()
            permissionCallbackCache.clear()
        }

        /**
         * 生成一个code。
         */
        private fun makeRequestCode(): Int {
            val code = randomGenerator.nextInt(0, 0x0001_0000)
            return if (permissionCallbackCache.indexOfKey(code) < 0) {
                code
            } else {
                makeRequestCode()
            }
        }
    }

    private inner class PermissionWrapper(val permissions: Array<out String>, val explain: String? = null)
}