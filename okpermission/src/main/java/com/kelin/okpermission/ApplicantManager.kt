package com.kelin.okpermission

import com.kelin.okpermission.applicant.PermissionsApplicant
import com.kelin.okpermission.permission.Permission
import com.kelin.okpermission.router.PermissionRouter

/**
 * **描述:** 权限申请器的管理器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  13:44
 *
 * **版本:** v 1.0.0
 */
internal class ApplicantManager(private val router: PermissionRouter, private var applicants: MutableCollection<PermissionsApplicant>, private val permissionApplicationDialog: ((permissions: Collection<Permission>, renewable: Renewable) -> Unit)?) {
    private val appliedApplicant: MutableList<PermissionsApplicant> = ArrayList(applicants.size)

    fun startApply(firstCall: Boolean, listener: (granted: Boolean, permissions: Array<out String>) -> Unit) {
        if (applicants.isNotEmpty()) {
            if (firstCall && permissionApplicationDialog != null) {
                val notGranted = applicants.flatMap { it.getNotGrantedPermissions() }
                if (notGranted.isNotEmpty()) {
                    permissionApplicationDialog.invoke(notGranted, object : Renewable {
                        override fun continueWorking(isContinue: Boolean) {
                            if (isContinue) {
                                doStartApply(listener)
                            } else {
                                listener(false, notGranted.map { it.permission }.toTypedArray())
                            }
                        }
                    })
                } else {
                    listener(true, emptyArray())
                }
            } else {
                doStartApply(listener)
            }
        } else {
            router.recycle()
            var isGranted = true
            val deniedPermissions = ArrayList<Permission>()
            appliedApplicant.forEach {
                if (!it.isGranted) {
                    isGranted = false
                }
                if (it.deniedPermissions.isNotEmpty()) {
                    deniedPermissions.addAll(it.deniedPermissions)
                }
            }
            listener(isGranted, deniedPermissions.map { it.permission }.toTypedArray())
        }
    }

    private fun doStartApply(listener: (granted: Boolean, permissions: Array<out String>) -> Unit) {
        val applicant = applicants.first()
        appliedApplicant.add(applicant)
        applicants.remove(applicant)
        applicant.applyPermission {
            startApply(false, listener)
        }
    }

    fun startCheck(): Array<out String> {
        val deniedPermissions = ArrayList<Permission>()
        applicants.forEach { deniedPermissions.addAll(it.checkDeniedPermissions) }
        return deniedPermissions.map { it.permission }.toTypedArray()
    }
}