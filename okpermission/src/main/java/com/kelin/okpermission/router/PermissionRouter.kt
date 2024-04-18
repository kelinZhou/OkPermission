package com.kelin.okpermission.router

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import com.kelin.okpermission.permission.Permission
import com.kelin.okpermission.permission.PermissionsCallback

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

    fun requestPermissions(permissions: Array<out Permission>, onResult: PermissionsCallback)

    fun shouldShowRequestPermissionRationale(permission: String): Boolean

    fun recycle()
}

internal class AndroidxPermissionRouter : AndroidxBasicRouter(), PermissionRouter {

    var manager: FragmentManager? = null

    private var callback: PermissionsCallback? = null
    private var permissionArray: Array<out Permission>? = null

    private lateinit var launcher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            callback?.invoke(permissionArray?.filter { result[it.permission] != true }?.toTypedArray() ?: emptyArray())
            callback = null
            permissionArray = null
        }
    }

    override fun requestPermissions(
        permissions: Array<out Permission>,
        onResult: PermissionsCallback
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            callback = onResult
            permissionArray = permissions
            launcher.launch(permissions.map { it.permission }.toTypedArray())
        } else {
            onResult(emptyArray())
        }
    }

    override fun recycle() {
        manager?.run {
            beginTransaction().remove(this@AndroidxPermissionRouter).commitAllowingStateLoss()
            executePendingTransactions()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        launcher.unregister()
    }
}