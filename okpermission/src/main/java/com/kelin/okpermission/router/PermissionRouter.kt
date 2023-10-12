package com.kelin.okpermission.router

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.FragmentManager
import com.kelin.okpermission.permission.Permission

/**
 * **描述:** 权限申请路由。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-06-30  10:11
 *
 * **版本:** v 1.0.0
 */
interface PermissionRouter {

    companion object {
        private const val ROUTER_TAG = "ok_permission_apply_permissions_router_tag"
        fun getAppRouter(fm: android.app.FragmentManager): PermissionRouter {
            return (fm.findFragmentByTag(ROUTER_TAG) as? PermissionRouter) ?: AppPermissionRouter().apply {
                manager = fm
                fm.beginTransaction()
                    .add(this, ROUTER_TAG)
                    .commitAllowingStateLoss()
                fm.executePendingTransactions()
            }
        }

        fun getAndroidxRouter(fm: FragmentManager, alternativeFm: FragmentManager? = null): PermissionRouter {
            return try {
                (fm.findFragmentByTag(ROUTER_TAG) as? PermissionRouter) ?: AndroidxPermissionRouter().apply {
                    manager = fm
                    fm.findFragmentByTag(ROUTER_TAG)
                    fm.beginTransaction()
                        .add(this, ROUTER_TAG)
                        .commitAllowingStateLoss()
                    fm.executePendingTransactions()
                }
            } catch (e: IllegalStateException) {
                if (alternativeFm != null) {
                    getAndroidxRouter(alternativeFm)
                } else {
                    throw e
                }
            }
        }
    }

    fun requestPermissions(permissions: Array<out Permission>, onResult: (permissions: Array<out Permission>) -> Unit)

    fun shouldShowRequestPermissionRationale(permission: String): Boolean

    fun recycle()
}

internal class AppPermissionRouter : AppBasicRouter(), PermissionRouter {

    var manager: android.app.FragmentManager? = null

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

    override fun recycle() {
        manager?.run {
            beginTransaction().remove(this@AppPermissionRouter).commitAllowingStateLoss()
            executePendingTransactions()
        }
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

internal class AndroidxPermissionRouter : AndroidxBasicRouter(), PermissionRouter {

    var manager: FragmentManager? = null

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

    override fun recycle() {
        manager?.run {
            beginTransaction().remove(this@AndroidxPermissionRouter).commitAllowingStateLoss()
            executePendingTransactions()
        }
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