package com.kelin.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue

/**
 * **描述:** 问题上报者。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-07-14  00:10
 *
 * **版本:** v 1.0.0
 */
class EleIssueRegistry: IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(PermissionDetector.ISSUE)
}