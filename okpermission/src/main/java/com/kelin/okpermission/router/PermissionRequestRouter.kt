package com.kelin.okpermission.router

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
interface PermissionRequestRouter {

    fun requestPermissions(permissions: Array<out Permission>, onResult: (permissions: Array<out Permission>) -> Unit)

    fun shouldShowRequestPermissionRationale(permission: String): Boolean
}