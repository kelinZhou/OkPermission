package com.kelin.okpermission

import java.io.Serializable

/**
 * **描述:** 可继续的。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-08-12  16:01
 *
 * **版本:** v 1.0.0
 */
interface Renewable : Serializable {
    fun continueWorking(isContinue: Boolean)
}