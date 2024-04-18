package com.kelin.okpermission.permission

/**
 * 权限回调。
 */
internal typealias PermissionsCallback = (permissions: Array<out Permission>) -> Unit

/**
 * ActivityResult回调。
 */
internal typealias ActivityResultCallback<D> = (resultCode: Int, data: D?, e: Exception?) -> Unit

/**
 * ActivityResult回调。
 */
internal typealias ActivityResultDataECallback<D> = (data: D?, e: Exception?) -> Unit

/**
 * ActivityResult回调。
 */
internal typealias ActivityResultCodeECallback = (resultCode: Int, e: Exception?) -> Unit

/**
 * ActivityResult回调。
 */
internal typealias ActivityResultCodeCallback = (resultCode: Int) -> Unit

/**
 * ActivityResult回调。
 */
internal typealias ActivityResultDataCallback<D> = (data: D?) -> Unit
