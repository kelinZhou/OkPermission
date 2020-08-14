package com.kelin.okpermission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.kelin.okpermission.applicant.*
import com.kelin.okpermission.applicant.intentgenerator.*
import com.kelin.okpermission.permission.Permission
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * **描述:** 权限申请的核心类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-06-29  20:06
 *
 * **版本:** v 1.0.0
 */
class OkPermission private constructor(private val weakActivity: WeakReference<Context>) {
    object permission {
        const val GPS = "kelin.permission.GPS"
        const val NOTIFICATION = "kelin.permission.NOTIFICATION"
    }

    companion object {

        private val BRAND = Build.MANUFACTURER.toLowerCase(Locale.CHINA)
        /**
         * 创建OkPermission并依附于Activity。
         *
         * @param context 当前的Activity对象。
         */
        fun with(context: Context): OkPermission {
            return if (context is Activity) {
                with(context)
            } else {
                throw ClassCastException("The context must be Activity")
            }
        }

        /**
         * 创建OkPermission并依附于Activity。
         *
         * @param activity 当前的Activity对象。
         */
        fun with(activity: Activity): OkPermission {
            return OkPermission(WeakReference(activity))
        }

        /**
         * 打开应用权限设置页面。由于这需要对各个品牌的手机进行适配，所以并不能保证一定能打开权限设置页面。
         * 如果无法打开应用权限设置页面，将会打开应用详情页面。
         *
         * @param context 需要Activity的Context。
         */
        fun gotoPermissionSettingPage(context: Context) {
            val intentGenerator = createSettingIntentGenerator()
            try {
                context.startActivity(intentGenerator.generatorIntent(context))
            } catch (e: Exception) {
                context.startActivity(intentGenerator.generatorAppDetailIntent(context))
            }
        }

        /**
         * 打开安装APK权限设置页面。
         * @param context 需要Activity的Context。
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun gotoInstallPermissionPage(context: Context) {
            context.startActivity(
                createSettingIntentGenerator(Permission.createDefault(Manifest.permission.REQUEST_INSTALL_PACKAGES)).generatorIntent(
                    context
                )
            )
        }

        /**
         * 打开通知权限设置页面。
         * @param context 需要Activity的Context。
         * @param channel 是否打开指定channel的设置页面，如果是该参数需要传指定的channel。
         */
        fun gotoNotificationPermissionPage(context: Context, channel: String = "") {
            context.startActivity(
                createSettingIntentGenerator(Permission.createNotification(channel))
                    .generatorIntent(context)
            )
        }

        /**
         * 打开悬浮窗权限设置页面。
         * @param context 需要Activity的Context。
         */
        fun gotoSystemWindowPermissionPage(context: Context) {
            context.startActivity(
                createSettingIntentGenerator(Permission.createDefault(Manifest.permission.SYSTEM_ALERT_WINDOW))
                    .generatorIntent(context)
            )
        }

        /**
         * 打开修改系统设置页面。
         * @param context 需要Activity的Context。
         */
        fun gotoWriteSettingsPage(context: Context) {
            context.startActivity(
                createSettingIntentGenerator(Permission.createDefault(Manifest.permission.WRITE_SETTINGS))
                    .generatorIntent(context)
            )
        }

        /**
         * 打开GPS开关页面。
         * @param context 需要Activity的Context。
         */
        fun gotoGPSSettingsPage(context: Context) {
            context.startActivity(
                createSettingIntentGenerator(Permission.createDefault(permission.GPS)).generatorIntent(context)
            )
        }

        /**
         * Check that certain permissions are registered in the manifest file.
         */
        private fun checkPermissionsRegistered(context: Context, vararg permissions: String) {
            val registeredPermissions = getManifestPermissions(context)
            val unregisteredPermissions = permissions.filter { !registeredPermissions.contains(it) && !it.startsWith("kelin.permission") }.toMutableList()
            if (unregisteredPermissions.isNotEmpty()) {
                throw IllegalStateException(
                    if (unregisteredPermissions.isNotEmpty()) {
                        "There are some permissions aren't registered in the manifest file! The following:\n${
                        unregisteredPermissions.joinToString("\n")
                        }"
                    } else {
                        ""
                    }
                )
            }
        }

        private fun getManifestPermissions(context: Context): Array<out String> {
            return context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
                .requestedPermissions
        }

        private fun createSettingIntentGenerator(permission: Permission? = null): SettingIntentGenerator {
            return when {
                BRAND.contains("huawei") -> EMUISettingsIntentGenerator(permission)
                BRAND.contains("xiaomi") -> MIUISettingsIntentGenerator(permission)
                BRAND.contains("oppo") -> OPPOSettingsIntentGenerator(permission)
                BRAND.contains("vivo") -> VIVOSettingsIntentGenerator(permission)
                BRAND.contains("meizu") -> MeiZuSettingsIntentGenerator(permission)
                BRAND.contains("sony") -> SonySettingsIntentGenerator(permission)
                BRAND.contains("lg") -> LGSettingsIntentGenerator(permission)
                BRAND.contains("lemobile") -> LSSettingsIntentGenerator(permission)
                BRAND.contains("360") -> Safe360SettingsIntentGenerator(permission)
                BRAND.contains("samsung") -> SamSungSettingsIntentGenerator(permission)
                BRAND.contains("smartisan") -> SMARTISANSettingsIntentGenerator(permission)
                else -> AppDetailIntentGenerator(permission)
            }
        }
    }

    private val needPermissions = ArrayList<Permission>()
    /**
     * 检测权限类型的拦截器。
     */
    private var checkPermissionTypeInterceptor: MakeApplicantInterceptor? = null

    private var missingPermissionDialogInterceptor: ((renewable: Renewable) -> Unit)? = null
    private var settingsIntentGeneratorInterceptor: ((permission: Permission) -> SettingIntentGenerator?)? = null

    private val context: Context?
        get() = weakActivity.get()


    /**
     * **添加弱申请权限。**
     *
     * 所谓弱申请就是指只尝试申请一次如果用户拒绝了就不会在本次就不会在提示用户进行授权。
     * 如果你的权限是必须的你可以调用```addForcePermissions```，如你希望尽可能的初始用户同意你的权限但有不希望有不好的体验
     * 你可以调用```addDefaultPermissions```。
     *
     * @param permissions 要进行弱申请的权限。
     *
     * @see addDefaultPermissions
     *
     * @see addForcePermissions
     */
    fun addWeakPermissions(vararg permissions: String): OkPermission {
        val context = context
        if (context != null) {
            checkPermissionsRegistered(context, *permissions)
            needPermissions.addAll(permissions.map { Permission.createWeak(it, true) })
        }
        return this
    }

    /**
     * **添加申请权限。**
     *
     * 通过该方法添加的权限会尽量的引导用户进行授权。
     * 如果你的权限是必须的你可以调用```addForcePermissions```，如你要申请的权限用户受不授权根本无关痛痒
     * 用户授权更好不授权也不会阻塞流程则建议调用```addWeakPermissions```。
     *
     * @param permissions 要进行弱申请的权限。
     *
     * @see addWeakPermissions
     *
     * @see addForcePermissions
     */
    fun addDefaultPermissions(vararg permissions: String): OkPermission {
        val context = context
        if (context != null) {
            checkPermissionsRegistered(context, *permissions)
            needPermissions.addAll(permissions.map { Permission.createDefault(it, false) })
        }
        return this
    }

    /**
     * **添加强制申请权限，也就是必要权限。**
     *
     * 通过该方法添加的权限会强制用户进行授权，如果用户不授权则会一直引导用户进行授权。
     * 如你希望尽可能的初始用户同意你的权限但有不希望有不好的体验你可以调用```addDefaultPermissions```，
     * 如你要申请的权限用户受不授权根本无关痛痒用户授权更好不授权也不会阻塞流程则建议调用```addWeakPermissions```。
     *
     * @param permissions 要进行弱申请的权限。
     *
     * @see addWeakPermissions
     *
     * @see addDefaultPermissions
     */
    fun addForcePermissions(vararg permissions: String): OkPermission {
        val context = context
        if (context != null) {
            checkPermissionsRegistered(context, *permissions)
            needPermissions.addAll(permissions.map { Permission.createDefault(it, true) })
        }
        return this
    }

    /**
     * 添加通知权限。
     *
     * @param necessary 是否是必要权限。
     * @param channels 要检查或申请的通知权限的Channel。虽然Channel的概念是Android8.0才出现的而你调用改方法时
     * 无需判断当前是什么版本的系统，只需要将你适配了的Channel传进来就行了。如果当前版>=8.0才会去检测这些Channel。
     */
    fun addNotificationPermission(necessary: Boolean = false, vararg channels: String): OkPermission {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && channels.isNotEmpty()) {
            needPermissions.addAll(channels.map { Permission.createNotification(it, necessary) })
        } else {
            needPermissions.add(Permission.createDefault(permission.NOTIFICATION, necessary))
        }
        return this
    }

    /**
     * 检测并申请权限，每当你需要使用必须申请权限在能使用的功能时你无需关心权限是否已经被用户授权，你只需要将你需要的权限
     * 通过```add***Permissions```方法添加进来，然后调用该方法即可。调用该方法后会首先去检测权限是否已经被授权使用，如果已经
     * 被授权使用则直接进行回调，如果没有或部分权限没有被授权使用则会针对没有授权的权限进行申请，等用户操作完毕后再进行回调。
     *
     * 注意：如果没有通过```add***Permissions```方法添加权限的话会检测清单文件中注册了的所有权限。
     */
    fun checkAndApplyOnly() {
        doOnApplyPermission { _, _ -> }
    }

    /**
     * 检测并申请权限，每当你需要使用必须申请权限在能使用的功能时你无需关心权限是否已经被用户授权，你只需要将你需要的权限
     * 通过```add***Permissions```方法添加进来，然后调用该方法即可。调用该方法后会首先去检测权限是否已经被授权使用，如果已经
     * 被授权使用则直接进行回调，如果没有或部分权限没有被授权使用则会针对没有授权的权限进行申请，等用户操作完毕后再进行回调。
     *
     * 注意：如果没有通过```add***Permissions```方法添加权限的话会检测清单文件中注册了的所有权限。
     *
     * @param onApplyFinished
     *
     * **第一个(Boolean)参数：** 回调方法中有两个参数，通常情况下你只需要关心第一个(Boolean)参数即可，这个参数是告诉你用户是否同意了你本次的权限请求，
     * 如果为true则表示用户已经同意，否则则表示用于没有同意或没有全部同意(如果你本次请求的是多个权限的话)，这里所说的全部只是
     * 指必要权限，即通过```addForcePermissions```方法添加的权限。其实你可以这么理解，如果你本次申请权限调用了```addForcePermissions```
     * 方法添加了一些权限，那么如果这些权限中任何一个权限被拒绝则改参数的值则为false，否则即为true。
     * 如果你没有调用```addForcePermissions```方法那个只有所申请的全部权限都被赋予改参数才会为true，否者即为false。
     *
     * **第二个(Array<out String>)参数：** 相比第一个参数第二个参数就简单的多了，他总是一个容纳了哪些被用户拒绝了的权限。你可以根据
     * 这个容器中的内容判断你所关心的权限是否被授权使用了，如果该容器中包含了你关心的权限那么就是该权限被拒绝了，否则就是被授权使用了。
     *
     * @see addWeakPermissions
     *
     * @see addDefaultPermissions
     *
     * @see addForcePermissions
     */
    fun checkAndApply(onApplyFinished: (granted: Boolean, permissions: Array<out String>) -> Unit) {
        doOnApplyPermission(onApplyFinished)
    }

    /**
     * 检查权限是否已经被授权，你需要将你需要的检查权限通过```add***Permissions```方法添加进来，然后调用该方法即可。
     *
     * 注意：如果没有通过```add***Permissions```方法添加权限的话会检测清单文件中注册了的所有权限。
     *
     * @param onApplyFinished 回调函数。
     *
     * **第一个(Boolean)参数：** 回调方法中有两个参数，通常情况下你只需要关心第一个(Boolean)参数即可，这个参数是告诉你用户是否已经授权了
     * 你所检查的权限。如果为true则表示用户已经同意了所有权限，否则则表示用于没有同意或没有全部同意(如果你本次检查的是多个权限的话)，
     *
     * **第二个(Array<out String>)参数：** 该参数包含了所有的被拒绝了的权限，如果第一个参数的值为true的话该参数才会有值，否者将是一个空的数组。
     */
    fun check(): Array<out String> {
        val activity = context
        return if (activity != null) {
            if (needPermissions.isEmpty()) {
                needPermissions.addAll(getManifestPermissions(activity).map { Permission.createDefault(it, false) })
            }
            createApplicantManager(activity)?.startCheck() ?: emptyArray()
        } else {
            needPermissions.map { it.permission }.toTypedArray()
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
    fun interceptSettingsIntentGenerator(interceptor: (permission: Permission) -> SettingIntentGenerator?): OkPermission {
        settingsIntentGeneratorInterceptor = interceptor
        return this
    }

    /**
     * 拦截设置页面Intent生产器，由自己实现指定页面的跳转。该方法必须要在apply等相关申请权限的方法前调用。
     */
    fun interceptCheckPermissionType(interceptor: MakeApplicantInterceptor): OkPermission {
        checkPermissionTypeInterceptor = interceptor
        return this
    }

    private fun doOnApplyPermission(onApplyFinished: (granted: Boolean, permissions: Array<out String>) -> Unit) {
        val activity = context
        if (activity != null) {
            if (needPermissions.isEmpty()) {
                needPermissions.addAll(getManifestPermissions(activity).map { Permission.createDefault(it, false) })
            }
            createApplicantManager(activity)?.startApply(onApplyFinished)
        }
    }

    private fun createApplicantManager(context: Context): ApplicantManager? {
        val applicants = HashMap<Class<out PermissionsApplicant>, PermissionsApplicant>()
        needPermissions.forEach {
            val applicantClass = if (checkPermissionTypeInterceptor?.interceptMake(it) == true) {
                checkPermissionTypeInterceptor!!.makeApplicant(it)
            } else {
                when (it.permission) {
                    Manifest.permission.REQUEST_INSTALL_PACKAGES -> {
                        ApkInstallApplicant::class.java
                    }
                    Manifest.permission.SYSTEM_ALERT_WINDOW -> {
                        SystemWindowApplicant::class.java
                    }
                    permission.NOTIFICATION -> {
                        NotificationApplicant::class.java
                    }
                    Manifest.permission.WRITE_SETTINGS -> {
                        WriteSettingsApplicant::class.java
                    }
                    permission.GPS -> {
                        GPSApplicant::class.java
                    }
                    else -> {
                        DefaultApplicant::class.java
                    }
                }
            }
            val a = applicants[applicantClass]
            if (a == null) {
                val applicant = applicantClass.getConstructor(Activity::class.java).newInstance(context)
                applicant.intentGenerator = settingsIntentGeneratorInterceptor?.invoke(it) ?: createSettingIntentGenerator(it)
                applicant.addPermission(it)
                applicant.missingPermissionDialogInterceptor = missingPermissionDialogInterceptor
                applicants[applicantClass] = applicant
            } else {
                a.addPermission(it)
                a.missingPermissionDialogInterceptor = missingPermissionDialogInterceptor
            }
        }
        return ApplicantManager(applicants.values)
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