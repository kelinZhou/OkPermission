package com.kelin.okpermission.permission

import com.kelin.okpermission.OkPermission

/**
 * **描述:** 定义权限接口。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-07-17  15:06
 *
 * **版本:** v 1.0.0
 */
abstract class Permission {

    abstract val permission: String
    abstract val necessary: Boolean
    abstract val isWeak: Boolean

    companion object {
        /**
         * 创建一个Permission对象。
         *
         * @param permission 要申请的权限。
         * @param necessary 是否是必要的权限。
         */
        internal fun createDefault(permission: String, necessary: Boolean = false): Permission {
            return DefaultPermission(permission, necessary, false)
        }

        /**
         * 创建一个Permission对象。
         *
         * @param permission 要申请的权限。
         * @param weak 是否是弱申请权限，弱申请的权限是优先级最弱的，当用户决绝该权限后就不会有任何提示去引导用户授予。
         */
        internal fun createWeak(permission: String, weak: Boolean): Permission {
            return DefaultPermission(permission, false, weak)
        }

        /**
         * 创建一个Permission对象。
         *
         * @param necessary 是否是必要的权限。
         */
        internal fun createNotification(channel: String, necessary: Boolean = false): Permission {
            return NotificationPermission(necessary, false, channel)
        }
    }

    private data class DefaultPermission(
        override val permission: String,
        override val necessary: Boolean,
        override val isWeak: Boolean
    ) : Permission()

    internal data class NotificationPermission(
        override val necessary: Boolean,
        override val isWeak: Boolean,
        val channel: String
    ) : Permission() {
        override val permission: String = OkPermission.permission.NOTIFICATION
    }
}