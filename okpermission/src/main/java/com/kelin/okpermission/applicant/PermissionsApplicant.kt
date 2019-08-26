package com.kelin.okpermission.applicant

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.util.SparseArray
import com.kelin.okpermission.OkActivityResult
import com.kelin.okpermission.Renewable
import com.kelin.okpermission.applicant.intentgenerator.AppDetailIntentGenerator
import com.kelin.okpermission.applicant.intentgenerator.SettingIntentGenerator
import com.kelin.okpermission.permission.Permission
import com.kelin.okpermission.router.BasicRouter
import com.kelin.okpermission.router.PermissionRequestRouter
import com.kelin.okpermission.router.SupportBasicRouter

/**
 * **描述:** 权限申请器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-15  15:12
 *
 * **版本:** v 1.0.0
 */
abstract class PermissionsApplicant(protected val activity: Activity) {
    private val permissionList: MutableList<Permission> = ArrayList()
    protected val applicationContext: Context
        get() = activity.applicationContext
    internal lateinit var intentGenerator: SettingIntentGenerator
    internal var isGranted: Boolean = true
    internal val deniedPermissions = ArrayList<Permission>()
    internal var missingPermissionDialogInterceptor: ((renewable: Renewable) -> Unit)? = null

    private val permissions: Array<out Permission>
        get() = permissionList.toTypedArray()

    protected abstract fun checkSelfPermissions(permission: Permission): Boolean

    protected abstract fun shouldShowRequestPermissionRationale(
        router: PermissionRequestRouter,
        permission: Permission
    ): Boolean

    protected abstract fun requestPermissions(
        router: PermissionRequestRouter,
        permissions: Array<out Permission>,
        onResult: (permissions: Array<out Permission>) -> Unit
    )

    internal fun addPermission(permission: Permission) {
        permissionList.add(permission)
    }

    internal fun applyPermission(
        applyPermissions: Array<out Permission> = permissions,
        finished: () -> Unit
    ) {
        if (applyPermissions.all { checkSelfPermissions(it) }) {
            finished()
        } else {
            onRequestPermissions(applyPermissions, finished)
        }
    }

    private fun applyPermissionAgain(
        applyPermissions: Array<out Permission> = permissions,
        finished: () -> Unit
    ) {
        var needCheck = true
        val router = getRouter(activity)
        val deniedPermissions = ArrayList<Permission>()
        val canRequestPermissions = ArrayList<Permission>()
        for (permission in applyPermissions) {
            val granted = checkSelfPermissions(permission)
            if (!granted) {
                isGranted = false
                if (!permission.isWeak) {
                    if (shouldShowRequestPermissionRationale(router, permission)) {
                        canRequestPermissions.add(permission)
                    } else {
                        deniedPermissions.add(permission)
                    }
                }
            }
        }
        if (canRequestPermissions.isEmpty() && deniedPermissions.isNotEmpty()) {
            needCheck = false
            showMissingPermissionDialog(applyPermissions, finished)
        }
        if (needCheck) {
            if (isGranted) {
                isGranted = true
                finished()
            } else {
                onRequestPermissions(applyPermissions, finished)
            }
        }
    }

    private fun onRequestPermissions(
        applyPermissions: Array<out Permission>,
        finished: () -> Unit
    ) {
        val router = getRouter(activity)
        requestPermissions(router, applyPermissions) { deniedPermissions ->
            when {
                //如果用户已经同意了全部权限
                deniedPermissions.isEmpty() -> {
                    isGranted = true
                    finished()
                }
                //如果全部都是弱申请
                deniedPermissions.all { it.isWeak } -> continueRequest(
                    false,
                    deniedPermissions,
                    finished
                )
                //如果有可继续申请的权限
                deniedPermissions.any { !it.isWeak && shouldShowRequestPermissionRationale(router, it) } -> {
                    val haveNecessary = deniedPermissions.any { it.necessary }
                    continueRequest(
                        haveNecessary,
                        if (haveNecessary) filterWeak(deniedPermissions) else deniedPermissions,
                        finished
                    )
                }
                //显示设置引导页面。
                else -> showMissingPermissionDialog(deniedPermissions, finished)
            }
        }
    }

    private fun continueRequest(
        isContinue: Boolean,
        deniedPermissions: Array<out Permission>,
        finished: () -> Unit
    ) {
        if (isContinue) {
            applyPermissionAgain(deniedPermissions, finished)
        } else {
            isGranted =
                if (permissionList.any { it.necessary }) !deniedPermissions.any { it.necessary } else deniedPermissions.isEmpty()
            this.deniedPermissions.addAll(deniedPermissions)
            finished()
        }
    }


    private fun showMissingPermissionDialog(
        deniedPermissions: Array<out Permission>,
        finished: () -> Unit
    ) {
        if (missingPermissionDialogInterceptor != null) {
            missingPermissionDialogInterceptor!!.invoke(object : Renewable {
                override fun continueWorking(isContinue: Boolean) {
                    onContinueWorking(isContinue, deniedPermissions, finished)
                }
            })
        } else {
            AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle("帮助")
                .setMessage("当前操作缺少必要权限。\n请点击\"设置\"-\"权限\"-打开所需权限。\n最后点击两次后退按钮，即可返回。")
                .setNegativeButton("退出") { _, _ ->
                    onContinueWorking(false, deniedPermissions, finished)
                }
                .setPositiveButton("设置") { _, _ ->
                    onContinueWorking(true, deniedPermissions, finished)
                }.show()
        }
    }

    private fun onContinueWorking(
        isContinue: Boolean,
        deniedPermissions: Array<out Permission>,
        finished: () -> Unit
    ) {
        if (isContinue) {
            OkActivityResult.instance.startActivityForResult(
                activity,
                intentGenerator.generatorIntent(activity)
            ) { _, _, e ->
                if (e == null) {
                    applyPermissionAgain(
                        filterWeak(deniedPermissions),
                        finished
                    )
                } else {
                    OkActivityResult.instance.startActivityForResult(
                        activity,
                        AppDetailIntentGenerator(null).generatorIntent(activity)
                    ) { _, _, exception ->
                        if (exception == null) {
                            applyPermissionAgain(
                                filterWeak(deniedPermissions),
                                finished
                            )
                        } else {
                            isGranted = false
                            this.deniedPermissions.clear()
                            this.deniedPermissions.addAll(deniedPermissions)
                        }
                    }
                }
            }
        } else {
            isGranted = false
            this.deniedPermissions.addAll(deniedPermissions)
            finished()
        }
    }


    protected fun filterWeak(permissions: Array<out Permission>): Array<out Permission> {
        val list = ArrayList<Permission>(permissions.size)
        permissions.forEach {
            if (it.isWeak) {
                deniedPermissions.add(it)
            } else {
                list.add(it)
            }
        }
        return list.toTypedArray()
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

    companion object {
        private const val ROUTER_TAG = "ok_permission_apply_permissions_router_tag"
    }

    internal class PermissionRouter : BasicRouter(), PermissionRequestRouter {
        private val permissionCallbackCache = SparseArray<CallbackWrapper>()


        override fun requestPermissions(
            permissions: Array<out Permission>,
            onResult: (permissions: Array<out Permission>) -> Unit
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val requestCode = makeRequestCode()
                permissionCallbackCache.put(requestCode, CallbackWrapper(permissions, onResult))
                requestPermissions(permissions.map { it.permission }.toTypedArray(), requestCode)
            } else {
                onResult(emptyArray())
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
            onResult: (permissions: Array<out Permission>) -> Unit
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val requestCode = makeRequestCode()
                permissionCallbackCache.put(requestCode, CallbackWrapper(permissions, onResult))
                requestPermissions(permissions.map { it.permission }.toTypedArray(), requestCode)
            } else {
                onResult(emptyArray())
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
        val callback: (permissions: Array<out Permission>) -> Unit
    ) {
        fun onResult(permissionArray: Array<String>, grantResults: IntArray) {
            grantResults.forEachIndexed { index, i ->
                if (i == PackageManager.PERMISSION_GRANTED) {
                    permissionArray[index] = ""
                }
            }
            callback(permissions.filter { permissionArray.contains(it.permission) }.toTypedArray())
        }
    }
}