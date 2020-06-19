package com.kelin.lint

import com.android.SdkConstants.TAG_MANIFEST
import com.android.SdkConstants.TAG_PERMISSION
import com.android.annotations.NonNull
import com.android.annotations.Nullable
import com.android.tools.lint.detector.api.*
import com.android.utils.XmlUtils
import com.google.common.collect.Maps
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UExpression
import org.w3c.dom.Document
import org.w3c.dom.Element
import sun.rmi.runtime.Log

/**
 * **描述:** 权限检测器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-07-14  00:04
 *
 * **版本:** v 1.0.0
 */
class PermissionDetector : Detector(), Detector.UastScanner {

    companion object {
        val ISSUE = Issue.create(
            "PermissionNotRegistered",
            "The permission not registered in the manifest file.",
            "Please register in the manifest file.",
            Category.CORRECTNESS,
            10,
            Severity.ERROR,
            Implementation(PermissionDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }


    private fun getManifestRegistrations(mergedManifest: Document?): List<String>? {
            if (mergedManifest == null || mergedManifest.documentElement == null) {
                return null
            }

            return XmlUtils.getSubTags(mergedManifest.documentElement).filter {
                it.tagName == TAG_PERMISSION
            }.mapNotNull { it.getAttribute("name") }
    }

    override fun getApplicableCallNames(): List<String>? {
        return listOf("applyPermissions", "forceApplyPermissions")
    }

    override fun getApplicableMethodNames(): List<String>? {
        return listOf("applyPermissions", "forceApplyPermissions")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        System.out.println("================进来了==============")
        com.android.ddmlib.Log.d("--------------", "================进来了==============")
        val registeredPermissions = getManifestRegistrations(context.mainProject.mergedManifest)
        if (context.evaluator.isMemberInClass(method, "com.kelin.okpermission.OkPermission")) {
            val arguments = node.valueArguments
            arguments.forEach {
                if (it.sourcePsi is KtNameReferenceExpression && registeredPermissions?.contains(it.sourcePsi?.text) != true) {
                    context.report(ISSUE,
                        it.sourcePsi!!,
                        context.getLocation(it.sourcePsi!!),
                        String.format("The permission<%1s> not registered in the manifest file.", it.sourcePsi?.text),
                        null)
                }
            }
        }
    }

    override fun visitMethod(
        context: JavaContext,
        visitor: JavaElementVisitor?,
        call: PsiMethodCallExpression,
        method: PsiMethod
    ) {
        System.out.println("================进来了==============2222")
        com.android.ddmlib.Log.d("--------------", "================进来了==============222")
        if (context.evaluator.isMemberInClass(method, "com.kelin.okpermission.OkPermission")) {
            val arguments = call.argumentList
        }
    }
}