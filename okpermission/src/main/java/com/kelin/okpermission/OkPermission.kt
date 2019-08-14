package com.kelin.okpermission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.SparseArray
import com.kelin.okpermission.permission.Permission
import com.kelin.okpermission.router.BasicRouter
import com.kelin.okpermission.router.PermissionRequestRouter
import com.kelin.okpermission.router.SupportBasicRouter
import java.lang.ref.WeakReference
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
class OkPermission private constructor(
    private val weakActivity: WeakReference<Activity>,
    private var explain: String?
) {
    companion object {
        private const val ROUTER_TAG = "ok_permission_apply_permissions_router_tag"

        /**
         * 创建OkPermission并依附于Activity。
         *
         * @param activity 当前的Activity对象。
         * @param explain 当用户拒绝权限后的解释。
         */
        fun with(activity: Activity, explain: String? = null): OkPermission {
            return OkPermission(WeakReference(activity), explain)
        }
    }

    private var missingPermissionDialogInterceptor: ((renewable: Renewable) -> Unit)? = null

    private val activity: Activity?
        get() = weakActivity.get()


    fun applyApkInstallPermission(canInstall: (canInstall: Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            applyPermissions(Manifest.permission.REQUEST_INSTALL_PACKAGES) {
                canInstall(it.isEmpty())
            }
        } else {
            canInstall(true)
        }
    }

    /**
     * **申请权限。**
     *
     * 当需要申请系统权限的时候调用，调用者无需关心要申请的权限是否已经被用户授予。只需无脑调用该方法即可。
     * 该方法会自动检测权限是否已经被授予，如果没有被授予则会去申请权限，如果已经被授予则会直接执行回调。
     *
     * @param permissions 要申请的权限。
     * @param listener 申请结果的监听。如果回调中的permissions为空则表示所有权限已经被获取，
     * 否则就表示用户拒绝了某个或某些权限，而被拒绝的权限就是permissions集合中的权限。
     *
     *
     * **注意：**
     * 该方法与 ```forceApplyPermissions``` 方法有以下两点不同：
     *
     * 1.调用该方法申请权限都是非必须的，也就是说无论用户是否授予或拒绝了所申请的权限都不应该影响流程。
     *
     * 2.该方法中所申请的权限默认只会提示用户一次，如果你设置了解释内容也顶多会在用户点击继续后再提示一次，如果用户已经勾选了
     * 不再询问的话则不会有任何提示展示给用户。
     *
     * **如果你想强制申请权限，请使用 ```forceApplyPermissions``` 方法。如果你一次申请了N个权限，而希望有些权限是强制的有些权限是为强制的
     * 的话可以使用 ```fun applyPermissions(Permission)``` 方法。**
     *
     * @see forceApplyPermissions
     *
     * @see mixApplyPermissions
     */
    fun weakApplyPermissions(
        vararg permissions: String,
        listener: (permissions: Array<out String>) -> Unit
    ) {
        val function: (Boolean, Array<out String>) -> Unit = { _, ps ->
            listener(ps)
        }
        val targetPermissions = permissions.map { Permission.createWeak(it, true) }.toTypedArray()
        checkPermissionsRegistered(targetPermissions, function)
        checkPermission(targetPermissions, function)
    }

    /**
     * **申请权限。**
     *
     * 当需要申请系统权限的时候调用，调用者无需关心要申请的权限是否已经被用户授予。只需无脑调用该方法即可。
     * 该方法会自动检测权限是否已经被授予，如果没有被授予则会去申请权限，如果已经被授予则会直接执行回调。
     *
     * @param permissions 要申请的权限。
     * @param listener 申请结果的监听。如果回调中的permissions为空则表示所有权限已经被获取，
     * 否则就表示用户拒绝了某个或某些权限，而被拒绝的权限就是permissions集合中的权限。
     *
     *
     * **注意：**
     * 该方法与 ```forceApplyPermissions``` 方法有以下两点不同：
     *
     * 1.调用该方法申请权限都是非必须的，也就是说无论用户是否授予或拒绝了所申请的权限都不应该影响流程。
     *
     * 2.该方法中所申请的权限默认只会提示用户一次，如果你设置了解释内容也顶多会在用户点击继续后再提示一次，如果用户已经勾选了
     * 不再询问的话则不会有任何提示展示给用户。
     *
     * **如果你想强制申请权限，请使用 ```forceApplyPermissions``` 方法。如果你一次申请了N个权限，而希望有些权限是强制的有些权限是为强制的
     * 的话可以使用 ```fun applyPermissions(Permission)``` 方法。**
     *
     * @see forceApplyPermissions
     *
     * @see mixApplyPermissions
     */
    fun applyPermissions(
        vararg permissions: String,
        listener: (permissions: Array<out String>) -> Unit
    ) {
        val function: (Boolean, Array<out String>) -> Unit = { _, ps ->
            listener(ps)
        }
        val targetPermissions = permissions.map { Permission.createDefault(it, false) }.toTypedArray()
        checkPermissionsRegistered(targetPermissions, function)
        checkPermission(targetPermissions, function)
    }

    /**
     * **申请权限。**
     *
     * 当需要申请系统权限的时候调用，调用者无需关心要申请的权限是否已经被用户授予。只需无脑调用该方法即可。
     * 该方法会自动检测权限是否已经被授予，如果没有被授予则会去申请权限，如果已经被授予则会直接执行回调。
     *
     * @param permissions 要申请的权限。
     * @param listener 申请结果的监听。如果回调中的permissions为空则表示所有权限已经被获取，
     * 否则就表示用户拒绝了某个或某些权限，而被拒绝的权限就是permissions集合中的权限。
     *
     *
     * **注意：**
     * 该方法与 applyPermissions 方法有以下两点不同：
     *
     * 1.调用该方法申请权限都是必须的，也就是说只要其中任何一个权限被用户拒绝都应该中断流程。
     *
     * 2.该方法中所申请的权限会一直提示用户授权直到用户授予了所申请的全部权限。除非用户点击了不再询问才有可能终止询问。
     *
     * **如果你想强制申请权限，请使用 ```forceApplyPermissions``` 方法。如果你一次申请了N个权限，而希望有些权限是强制的有些权限是为强制的
     * 的话可以使用 ```fun applyPermissions(Permission)``` 方法。**
     *
     * @see applyPermissions
     *
     * @see mixApplyPermissions
     */
    fun forceApplyPermissions(
        vararg permissions: String,
        listener: (permissions: Array<out String>) -> Unit
    ) {
        val function: (Boolean, Array<out String>) -> Unit = { _, ps ->
            listener(ps)
        }
        val targetPermissions = permissions.map { Permission.createDefault(it, true) }.toTypedArray()
        checkPermissionsRegistered(targetPermissions, function)
        checkPermission(targetPermissions, function)
    }


    /**
     * **混合申请权限。**
     *
     * 所谓混合是指同事申请必要权限和非必要权限。
     * 当需要申请系统权限的时候调用，调用者无需关心要申请的权限是否已经被用户授予。只需无脑调用该方法即可。
     * 该方法会自动检测权限是否已经被授予，如果没有被授予则会去申请权限，如果已经被授予则会直接执行回调。
     *
     * @param permissions 要申请的权限。
     * @param listener 申请结果的监听。如果回调中的permissions为空则表示所有权限已经被获取，
     * 否则就表示用户拒绝了某个或某些权限，而被拒绝的权限就是permissions集合中的权限。
     */
    fun mixApplyPermissions(
        vararg permissions: Permission,
        listener: (granted: Boolean, permissions: Array<out String>) -> Unit
    ) {
        checkPermissionsRegistered(permissions, listener)
        checkPermission(permissions, listener)
    }

    private fun checkPermissionsRegistered(
        permissions: Array<out Permission>,
        listener: (granted: Boolean, permissions: Array<out String>) -> Unit
    ) {
        val context = activity
        if (context != null) {
            val registeredPermissions =
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
                    .requestedPermissions
            val unregisteredPermissions = permissions.filter { !registeredPermissions.contains(it.permission) }
            if (unregisteredPermissions.isNotEmpty()) {
                throw IllegalStateException(
                    "There are some permissions aren't registered in the manifest file! The following:\n${unregisteredPermissions.joinToString(
                        "\n"
                    )}\n"
                )
            }
        } else {
            listener(!permissions.any { it.necessary }, permissions.map { it.permission }.toTypedArray())
        }
    }

    fun interceptMissingPermissionDialog(interceptor: (renewable: Renewable) -> Unit): OkPermission {
        missingPermissionDialogInterceptor = interceptor
        return this
    }

    /**
     * 检测权限授予情况。如果所有权限已被授予则直接回调，否则就去尝试获取权限。
     *
     * @param permissions 要申请的权限。
     * @param listener 申请结果的监听。如果回调中的permissions为空则表示所有权限已经被获取，
     * 否则就表示用户拒绝了某个或某些权限，而被拒绝的权限就是permissions集合中的权限。
     */
    private fun checkPermission(
        permissions: Array<out Permission>,
        listener: (granted: Boolean, permissions: Array<out String>) -> Unit
    ) {
        val activity = activity
        if (activity != null) {
            if (permissions.size == 1 && permissions[0].permission == Manifest.permission.REQUEST_INSTALL_PACKAGES) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !activity.packageManager.canRequestPackageInstalls()) {
                    OkActivityResult.instance.startActivityForResult(
                        activity,
                        Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${activity.packageName}"))
                    ) { _, _ ->
                        if (activity.packageManager.canRequestPackageInstalls()) {
                            listener(true, emptyArray())
                        } else {
                            listener(false, arrayOf(Manifest.permission.REQUEST_INSTALL_PACKAGES))
                        }
                    }
                } else {
                    listener(true, emptyArray())
                }
            } else {
                if (missingPermissionDialogInterceptor == null) {
                    missingPermissionDialogInterceptor = {
                        AlertDialog.Builder(activity)
                            .setCancelable(false)
                            .setTitle("帮助")
                            .setMessage("当前操作缺少必要权限。\n请点击\"设置\"-\"权限\"-打开所需权限。\n最后点击两次后退按钮，即可返回。")
                            .setNegativeButton("退出") { _, _ ->
                                it.continueWorking(false)
                            }
                            .setPositiveButton("设置") { _, _ ->
                                it.continueWorking(true)
                            }.show()
                    }
                }

                val deniedPermissions = ArrayList<Permission>()
                permissions.forEach { permission ->
                    if (ContextCompat.checkSelfPermission(
                            activity,
                            permission.permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        deniedPermissions.add(permission)
                    }
                }
                if (deniedPermissions.isEmpty()) {
                    listener(true, emptyArray())
                } else {
                    requestPermissions(
                        deniedPermissions.toTypedArray(),
                        listener
                    )
                }
            }
        }
    }

    private fun requestPermissions(
        deniedPermissions: Array<out Permission>,
        listener: (granted: Boolean, permissions: Array<out String>) -> Unit
    ) {
        val context = activity
        if (context != null) {
            getRouter(context).requestPermissions(deniedPermissions) { permissions, grantResults ->
                val hadAllPermission = !(grantResults.any { it != PackageManager.PERMISSION_GRANTED })
                if (hadAllPermission) {
                    listener(true, emptyArray())
                } else {
                    processingPartialPermissionsDenied(
                        context,
                        permissions,
                        listener
                    )
                }
            }
        }
    }

    private fun processingPartialPermissionsDenied(
        context: Activity,
        deniedPermissions: Array<out Permission>,
        listener: (granted: Boolean, permissions: Array<out String>) -> Unit
    ) {
        val router = getRouter(context)
        if (deniedPermissions.any { router.shouldShowRequestPermissionRationale(it.permission) }) { //如果有可以继续申请的权限
            val text = explain
            if (!text.isNullOrEmpty()) { //如果有解释内容
                explain = null
                //展示解释内容
                showRequestPermissionsExplain(text) { isContinue ->
                    continueRequest(
                        isContinue,
                        deniedPermissions,
                        listener
                    )
                }
            } else {
                continueRequest(
                    deniedPermissions.any { it.necessary },
                    deniedPermissions,
                    listener
                )
            }
        } else {
            val text = explain
            if (!text.isNullOrEmpty()) {
                explain = null
                showRequestPermissionsExplain(text) { isContinue ->
                    if (isContinue) {
                        showMissingPermissionDialog(
                            deniedPermissions,
                            listener
                        )
                    } else {
                        continueRequest(
                            false,
                            deniedPermissions,
                            listener
                        )
                    }
                }
            } else {
                if (deniedPermissions.all { it.isWeak }) {  //如果全部都是弱申请
                    continueRequest(
                        false,
                        deniedPermissions,
                        listener
                    )
                } else {
                    showMissingPermissionDialog(
                        deniedPermissions,
                        listener
                    )
                }
            }
        }
    }

    private fun continueRequest(
        isContinue: Boolean,
        deniedPermissions: Array<out Permission>,
        listener: (granted: Boolean, permissions: Array<out String>) -> Unit
    ) {
        if (isContinue) {
            requestPermissions(deniedPermissions, listener)
        } else {
            listener(
                !deniedPermissions.any { it.necessary },
                deniedPermissions.map { it.permission }.toTypedArray()
            )
        }
    }

    private fun showRequestPermissionsExplain(
        explain: String,
        listener: (isContinue: Boolean) -> Unit
    ) {
        val activity = activity
        if (activity != null) {
            AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle("提示")
                .setMessage(explain)
                .setNegativeButton("取消") { _, _ -> listener(false) }
                .setPositiveButton("继续") { _, _ -> listener(true) }
                .show()
        }
    }

    private fun showMissingPermissionDialog(
        deniedPermissions: Array<out Permission>,
        listener: (granted: Boolean, permissions: Array<out String>) -> Unit
    ) {
        val interceptor = missingPermissionDialogInterceptor
        if (interceptor != null) {
            interceptor.invoke(object : Renewable {
                override fun continueWorking(isContinue: Boolean) {
                    if (isContinue) {
                        val activity = activity
                        if (activity != null) {
                            OkActivityResult.instance.startActivityForResult(
                                activity,
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + activity.packageName)
                                )
                            ) { _, _ ->
                                requestPermissions(deniedPermissions, listener)
                            }
                        }
                    } else {
                        listener(false, deniedPermissions.map { it.permission }.toTypedArray())
                    }
                }
            })
        } else {
            throw NullPointerException("The missingPermissionDialogInterceptor is Null object.")
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

    internal class PermissionRouter : BasicRouter(), PermissionRequestRouter {
        private val permissionCallbackCache = SparseArray<CallbackWrapper>()

        override fun requestPermissions(
            permissions: Array<out Permission>,
            onResult: (permissions: Array<out Permission>, grantResults: IntArray) -> Unit
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val requestCode = makeRequestCode()
                permissionCallbackCache.put(requestCode, CallbackWrapper(permissions, onResult))
                requestPermissions(permissions.map { it.permission }.toTypedArray(), requestCode)
            } else {
                onResult(emptyArray(), IntArray(0))
            }
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            val callback = permissionCallbackCache[requestCode]
            permissionCallbackCache.remove(requestCode)
            callback?.onResult(permissions, grantResults)
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

        private val permissionCallbackCache = SparseArray<CallbackWrapper>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        override fun requestPermissions(
            permissions: Array<out Permission>,
            onResult: (permissions: Array<out Permission>, grantResults: IntArray) -> Unit
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val requestCode = makeRequestCode()
                permissionCallbackCache.put(requestCode, CallbackWrapper(permissions, onResult))
                requestPermissions(permissions.map { it.permission }.toTypedArray(), requestCode)
            } else {
                onResult(emptyArray(), IntArray(0))
            }
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            val callback = permissionCallbackCache[requestCode]
            permissionCallbackCache.remove(requestCode)
            callback?.onResult(permissions, grantResults)
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

    private class CallbackWrapper(
        val permissions: Array<out Permission>,
        val callback: (permissions: Array<out Permission>, grantResults: IntArray) -> Unit
    ) {
        fun onResult(permissions: Array<String>, grantResults: IntArray) {
            grantResults.forEachIndexed { index, i ->
                if (i == PackageManager.PERMISSION_GRANTED) {
                    permissions[index] = ""
                }
            }
            callback(
                this.permissions.filter { permissions.contains(it.permission) }.toTypedArray(), grantResults
            )
        }
    }
}