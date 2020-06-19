package com.kelin.okpermission

import com.kelin.okpermission.applicant.PermissionsApplicant
import com.kelin.okpermission.permission.Permission

/**
 * **描述:** 权限申请器的管理器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-19  13:44
 *
 * **版本:** v 1.0.0
 */
class ApplicantManager(private var applicants: MutableCollection<PermissionsApplicant>) {
    private val appliedApplicant: MutableList<PermissionsApplicant> = ArrayList(applicants.size)

    fun startApply(listener: (granted: Boolean, permissions: Array<out String>) -> Unit) {
        if (applicants.isNotEmpty()) {
            val applicant = applicants.first()
            appliedApplicant.add(applicant)
            applicants.remove(applicant)
            applicant.applyPermission {
                startApply(listener)
            }
        } else {
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

    fun startCheck():Array<out String> {
        val deniedPermissions = ArrayList<Permission>()
        applicants.forEach { deniedPermissions.addAll(it.checkDeniedPermissions) }
        return deniedPermissions.map { it.permission }.toTypedArray()
    }
}