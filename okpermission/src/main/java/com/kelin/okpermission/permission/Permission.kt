package com.kelin.okpermission.permission

/**
 * **描述:** 定义权限接口。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-07-17  15:06
 *
 * **版本:** v 1.0.0
 */
interface Permission {
    val permission: String
    val necessary: Boolean

    companion object {
        /**
         * 创建一个Permission对象。
         *
         * @param permission 要申请的权限。
         * @param necessary 是否是必要的权限。如果你申请的所有的权限都是必要的权限建议使用 forceApplyPermissions 方法，
         * 如果你要申请的权限全部都是非必要的权限则建议使用 applyPermissions 方法。
         */
        fun create(permission: String, necessary: Boolean): Permission {
            return DefaultPermission(permission, necessary)
        }
    }

    data class DefaultPermission(override val permission: String, override val necessary: Boolean) : Permission
}