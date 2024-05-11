package com.kelin.okpermission

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import com.kelin.okpermission.permission.ActivityResultCodeECallback
import com.kelin.okpermission.permission.ActivityResultDataECallback
import com.kelin.okpermission.permission.ActivityResultCallback
import com.kelin.okpermission.permission.ActivityResultCodeCallback
import com.kelin.okpermission.permission.ActivityResultDataCallback
import com.kelin.okpermission.router.ActivityResultRouter
import com.kelin.okpermission.router.AppBasicRouter
import java.io.Serializable

/**
 * **描述:** startActivityForResult的帮助工具。。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-06-29  13:18
 *
 * **版本:** v 1.0.0
 */
object OkActivityResult {

    private const val KEY_RESULT_DATA = "ok_permission_activity_result_data"

    fun <D> startActivity(
        activity: Activity,
        clazz: Class<out Activity>,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultDataCallback<D>
    ) {
        startActivity(activity, Intent(activity, clazz), options, onResult)
    }

    fun <D> startActivity(
        activity: Activity,
        intent: Intent,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultDataCallback<D>
    ) {
        startActivityOrException<D>(activity, intent, options) { data, e ->
            if (e == null) {
                onResult(data)
            } else {
                Log.e("OkActivityResult", "The activity not fount! \n${e.message}")
                e.printStackTrace()
                onResult(null)
            }
        }
    }

    fun startActivityForCode(
        activity: Activity,
        clazz: Class<out Activity>,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultCodeCallback
    ) {
        startActivityForCode(activity, Intent(activity, clazz), options, onResult)
    }

    fun startActivityForCode(
        activity: Activity,
        intent: Intent,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultCodeCallback
    ) {
        startActivityForCodeOrException(activity, intent, options) { resultCode, e ->
            if (e == null) {
                onResult(resultCode)
            } else {
                Log.e("OkActivityResult", "The activity not fount! \n${e.message}")
                e.printStackTrace()
                onResult(Activity.RESULT_CANCELED)
            }
        }
    }

    fun <D> startActivityOrException(
        activity: Activity,
        clazz: Class<out Activity>,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultDataECallback<D>
    ) {
        startActivityOrException(activity, Intent(activity, clazz), options, onResult)
    }

    fun <D> startActivityOrException(
        context: Activity,
        intent: Intent,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultDataECallback<D>
    ) {
        getRouter<D>(context).startActivityForResult(intent, options) { _, data, e ->
            onResult(data, e)
        }
    }

    fun startActivityForCodeOrException(
        activity: Activity,
        clazz: Class<out Activity>,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultCodeECallback
    ) {
        startActivityForCodeOrException(activity, Intent(activity, clazz), options, onResult)
    }

    fun startActivityForCodeOrException(
        context: Activity,
        intent: Intent,
        options: ActivityOptionsCompat? = null,
        onResult: ActivityResultCodeECallback
    ) {
        getRouter<Any>(context).startActivityForResult(intent, options) { resultCode, _, e ->
            onResult(resultCode, e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <D> getResultData(intent: Intent): D? {
        return intent.extras?.get(KEY_RESULT_DATA).let { (it as? D) ?: intent as? D }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: Boolean = true, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: BooleanArray, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: Byte, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: ByteArray, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: Char, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: CharArray, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: Short, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: ShortArray, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: Int, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: IntArray, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: Long, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: LongArray?, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: Float, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: FloatArray, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: Double, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: DoubleArray, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: String, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: Array<out String>, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: CharSequence, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: Array<out CharSequence>, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: Parcelable, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: Array<out Parcelable>, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    @JvmOverloads
    fun setResultData(activity: Activity, data: Serializable?, finish: Boolean = true) {
        activity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_RESULT_DATA, data)
        })
        if (finish) {
            activity.finish()
        }
    }

    private fun <D> getRouter(activity: Activity): ActivityResultRouter<D> {
        return findRouter(activity) ?: createRouter(activity)
    }

    private fun <D> createRouter(activity: Activity): ActivityResultRouter<D> {
        val router: ActivityResultRouter<D>
        if (activity is FragmentActivity) {
            router = ActivityResultRouterImpl()
            val fm = activity.supportFragmentManager
            fm.beginTransaction()
                .add(router, null)
                .commitAllowingStateLoss()
            try {
                fm.executePendingTransactions()
            } catch (_: Exception) {
            }
        } else {
            throw IllegalAccessException("You have to use the androidx.fragment.app.FragmentActivity to startActivity for result.")
        }
        return router
    }

    @Suppress("UNCHECKED_CAST")
    private fun <D> findRouter(activity: Activity): ActivityResultRouter<D>? {
        return if (activity is FragmentActivity) {
            try {
                activity.supportFragmentManager.fragments.find {
                    it is ActivityResultRouter<*> && !it.isInUse
                } as? ActivityResultRouter<D>
            } catch (e: Exception) {
                null
            }
        } else {
            throw IllegalAccessException("You have to use the androidx.fragment.app.FragmentActivity to startActivity for result.")
        }
    }

    internal class ActivityResultRouterImpl<D> : AppBasicRouter(), ActivityResultRouter<D> {

        private lateinit var launcher: ActivityResultLauncher<Intent>

        private var callback: ActivityResultCallback<D>? = null

        override val isInUse: Boolean
            get() = callback != null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val resultCode = result.resultCode
                try {
                    if (resultCode == Activity.RESULT_OK) {
                        callback?.invoke(resultCode, result.data?.let { getResultData<D>(it) }, null)
                    } else {
                        callback?.invoke(resultCode, null, null)
                    }
                } catch (e: Exception) {
                    callback?.invoke(resultCode, null, e)
                } finally {
                    callback = null
                }
            }
        }

        override fun startActivityForResult(intent: Intent, options: ActivityOptionsCompat?, onResult: ActivityResultCallback<D>) {
            try {
                callback = onResult
                launcher.launch(intent, options)
            } catch (e: Exception) {
                onResult(Activity.RESULT_CANCELED, null, e)
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            launcher.unregister()
        }
    }
}