package com.kelin.okpermission.router

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

        fun getRouter(fm: FragmentManager, alternativeFm: FragmentManager? = null): PermissionRouter {
            return try {
                val available = fm.fragments.find {
                    it is PermissionRouter && !it.isInUse
                } as? PermissionRouter
                available ?: DefPermissionRouter().apply {
                    manager = fm
                    fm.beginTransaction()
                        .add(this, null)
                        .commitAllowingStateLoss()
                    fm.executePendingTransactions()
                }
            } catch (e: IllegalStateException) {
                if (alternativeFm != null) {
                    getRouter(alternativeFm)
                } else {
                    throw e
                }
            }
        }
    }

    val isInUse: Boolean

    fun requestPermissions(permissions: Array<out Permission>, onResult: PermissionsCallback)

    fun shouldShowRequestPermissionRationale(permission: String): Boolean

    fun recycle()
}

internal class DefPermissionRouter : AppBasicRouter(), PermissionRouter {

    var manager: FragmentManager? = null

    private var callback: PermissionsCallback? = null
    private var permissionArray: Array<out Permission>? = null

    private lateinit var launcher: ActivityResultLauncher<Array<String>>

    override val isInUse: Boolean
        get() = callback != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            try {
                callback?.invoke(permissionArray?.filter { result[it.permission] != true }?.toTypedArray() ?: emptyArray())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                callback = null
                permissionArray = null
            }
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
            beginTransaction().remove(this@DefPermissionRouter).commitAllowingStateLoss()
            executePendingTransactions()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        launcher.unregister()
    }
}