package com.kelin.okpermission

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import com.kelin.okpermission.applicant.ApkInstallApplicant
import com.kelin.okpermission.applicant.DefaultApplicant
import com.kelin.okpermission.applicant.PermissionsApplicant
import com.kelin.okpermission.intentgenerator.*
import com.kelin.okpermission.permission.Permission
import java.lang.ref.WeakReference

/**
 * **描述:** 权限申请的核心类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-06-29  20:06
 *
 * **版本:** v 1.0.0
 */
class OkPermission private constructor(private val weakActivity: WeakReference<Activity>) {
    companion object {

        /**
         * 创建OkPermission并依附于Activity。
         *
         * @param activity 当前的Activity对象。
         */
        fun with(activity: Activity): OkPermission {
            return OkPermission(WeakReference(activity))
        }
    }

    /**
     * 权限被授予。
     */
    private var permissionGranted: (() -> Unit)? = null
    /**
     * 权限被拒绝。
     */
    private var permissionDenied: (() -> Unit)? = null
    /**
     * 检测权限类型的拦截器。
     */
    private var checkPermissionTypeInterceptor: MakeApplicantInterceptor? = null

    private var missingPermissionDialogInterceptor: ((renewable: Renewable) -> Unit)? = null
    private var settingIntentGeneratorInterceptor: (() -> SettingIntentGenerator)? = null

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
        checkPermissionsRegistered(targetPermissions)
        doOnApplyPermission(targetPermissions, function)
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
        checkPermissionsRegistered(targetPermissions)
        doOnApplyPermission(targetPermissions, function)
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
        checkPermissionsRegistered(targetPermissions)
        doOnApplyPermission(targetPermissions, function)
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
        checkPermissionsRegistered(permissions)
        doOnApplyPermission(permissions, listener)
    }

    private fun checkPermissionsRegistered(permissions: Array<out Permission>) {
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
        }
    }

    /**
     * 拦截引导跳转到设置页面的弹窗，由自己实现弹窗。该方法必须要在apply等相关申请权限的方法前调用。
     */
    fun interceptMissingPermissionDialog(interceptor: (renewable: Renewable) -> Unit): OkPermission {
        missingPermissionDialogInterceptor = interceptor
        return this
    }

    /**
     * 拦截设置页面Intent生产器，由自己实现指定页面的跳转。该方法必须要在apply等相关申请权限的方法前调用。
     */
    fun interceptSettingIntentGenerator(interceptor: () -> SettingIntentGenerator): OkPermission {
        settingIntentGeneratorInterceptor = interceptor
        return this
    }

    /**
     * 检测权限授予情况。如果所有权限已被授予则直接回调，否则就去尝试获取权限。
     *
     * @param permissions 要申请的权限。
     * @param listener 申请结果的监听。如果回调中的permissions为空则表示所有权限已经被获取，
     * 否则就表示用户拒绝了某个或某些权限，而被拒绝的权限就是permissions集合中的权限。
     */
    private fun doOnApplyPermission(
        permissions: Array<out Permission>,
        listener: (granted: Boolean, permissions: Array<out String>) -> Unit
    ) {
        val activity = activity
        if (activity != null) {
            createApplicantManager(permissions)?.startApply(listener)
        }
    }

    private fun createApplicantManager(permissions: Array<out Permission>): ApplicantManager? {
        if (permissions.isEmpty()) {
            throw NullPointerException("The permission is empty!")
        }
        val context = activity
        if (context != null) {
            val applicants = HashMap<Class<out PermissionsApplicant>, PermissionsApplicant>()
            permissions.forEach {
                val applicantClass = if (checkPermissionTypeInterceptor?.interceptMake(it) == true) {
                    checkPermissionTypeInterceptor!!.makeApplicant(it)
                } else {
                    when (it.permission) {
                        Manifest.permission.REQUEST_INSTALL_PACKAGES -> {
                            ApkInstallApplicant::class.java
                        }
                        else -> {
                            DefaultApplicant::class.java
                        }
                    }
                }
                val a = applicants[applicantClass]
                if (a == null) {
                    val applicant = applicantClass.getConstructor(Activity::class.java).newInstance(context)
                    if (applicant is ApkInstallApplicant && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        applicant.apkInstallPermissionIntentGenerator = ApkInstallPermissionIntentGenerator()
                    }
                    applicant.intentGenerator =
                        settingIntentGeneratorInterceptor?.invoke() ?: createSettingIntentGenerator()
                    applicant.addPermission(it)
                    applicant.missingPermissionDialogInterceptor = missingPermissionDialogInterceptor
                    applicants[applicantClass] = applicant
                } else {
                    a.addPermission(it)
                    a.missingPermissionDialogInterceptor = missingPermissionDialogInterceptor
                }
            }
            return ApplicantManager(applicants.values)
        } else {
            return null
        }
    }

    private fun createSettingIntentGenerator(): SettingIntentGenerator {
        return EMUISettingsIntentGenerator()
    }

    /**
     * 创建权限申请器的拦截器。
     */
    interface MakeApplicantInterceptor {

        /**
         * 是否拦截，如果你希望拦截本次创建则返回true，否则应当返回false。只有在返回true的时候 makeApplicant方法才会执行。
         *
         * @param permission 你要申请的权限中的其中一个。
         */
        fun interceptMake(permission: Permission): Boolean

        /**
         * 根据一个权限返回其所对应的权限申请器。该方法只有在interceptMake方法返回true的时候再回被执行。
         *
         * @param permission 你要申请的权限中的其中一个。
         */
        fun makeApplicant(permission: Permission): Class<out PermissionsApplicant>
    }
}