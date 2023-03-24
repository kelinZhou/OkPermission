package com.kelin.okpermission

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.util.SparseArray
import androidx.fragment.app.FragmentActivity
import com.kelin.okpermission.router.ActivityResultRouter
import com.kelin.okpermission.router.AppBasicRouter
import com.kelin.okpermission.router.AndroidxBasicRouter
import java.io.Serializable
import java.lang.ClassCastException

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

    private const val ROUTER_TAG = "ok_permission_activity_result_router_tag"
    private const val KEY_RESULT_DATA = "ok_permission_activity_result_data"

    fun <D> startActivity(
        activity: Activity,
        clazz: Class<out Activity>,
        options: Bundle? = null,
        onResult: (data: D?) -> Unit
    ) {
        startActivity(activity, Intent(activity, clazz), options, onResult)
    }

    fun <D> startActivity(
        activity: Activity,
        intent: Intent,
        options: Bundle? = null,
        onResult: (data: D?) -> Unit
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
        options: Bundle? = null,
        onResult: (resultCode: Int) -> Unit
    ) {
        startActivityForCode(activity, Intent(activity, clazz), options, onResult)
    }

    fun startActivityForCode(
        activity: Activity,
        intent: Intent,
        options: Bundle? = null,
        onResult: (resultCode: Int) -> Unit
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
        options: Bundle? = null,
        onResult: (data: D?, e: Exception?) -> Unit
    ) {
        startActivityOrException(activity, Intent(activity, clazz), options, onResult)
    }

    fun <D> startActivityOrException(
        context: Activity,
        intent: Intent,
        options: Bundle? = null,
        onResult: (data: D?, e: Exception?) -> Unit
    ) {
        getRouter<D>(context).startActivityForResult(intent, options){ _, data, e ->
            onResult(data, e)
        }
    }

    fun startActivityForCodeOrException(
        activity: Activity,
        clazz: Class<out Activity>,
        options: Bundle? = null,
        onResult: (resultCode: Int, e: Exception?) -> Unit
    ) {
        startActivityForCodeOrException(activity, Intent(activity, clazz), options, onResult)
    }

    fun startActivityForCodeOrException(
        context: Activity,
        intent: Intent,
        options: Bundle? = null,
        onResult: (resultCode: Int, e: Exception?) -> Unit
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
            router = AndroidxActivityResultRouterImpl()
            val fm = activity.supportFragmentManager
            fm.beginTransaction()
                .add(router, ROUTER_TAG)
                .commitAllowingStateLoss()
            try {
                fm.executePendingTransactions()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            router = AppActivityResultRouterImpl()
            val fm = activity.fragmentManager
            fm.beginTransaction()
                .add(router, ROUTER_TAG)
                .commitAllowingStateLoss()
            try {
                fm.executePendingTransactions()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return router
    }

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    private fun <D> findRouter(activity: Activity): ActivityResultRouter<D>? {
        return if (activity is FragmentActivity) {
            try {
                activity.supportFragmentManager.findFragmentByTag(ROUTER_TAG) as? ActivityResultRouter<D>
            } catch (e: Exception) {
                null
            }
        } else {
            try {
                activity.fragmentManager.findFragmentByTag(ROUTER_TAG) as? ActivityResultRouter<D>
            } catch (e: Exception) {
                null
            }
        }
    }

    internal class AppActivityResultRouterImpl<D> : AppBasicRouter(), ActivityResultRouter<D> {

        private val resultCallbackCache = SparseArray<(resultCode: Int, data: D?, e: Exception?) -> Unit>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        override fun startActivityForResult(intent: Intent, options: Bundle?, onResult: (resultCode: Int, data: D?, e: Exception?) -> Unit) {
            try {
                val requestCode = generateRequestCode()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivityForResult(intent, requestCode, options)
                } else {
                    startActivityForResult(intent, requestCode)
                }
                resultCallbackCache.put(requestCode, onResult)
            } catch (e: Exception) {
                onResult(Activity.RESULT_CANCELED, null, e)
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            val callback = resultCallbackCache[requestCode]
            resultCallbackCache.remove(requestCode)
            try {
                callback?.invoke(resultCode, data?.let { getResultData<D>(it) }, null)
            } catch (e: ClassCastException) {
                callback?.invoke(resultCode, null, null)
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            resultCallbackCache.clear()
        }

        /**
         * 生成一个code。
         */
        private fun generateRequestCode(): Int {
            val code = randomGenerator.nextInt(0, 0x0001_0000)
            return if (resultCallbackCache.indexOfKey(code) < 0) {
                code
            } else {
                generateRequestCode()
            }
        }
    }

    internal class AndroidxActivityResultRouterImpl<D> : AndroidxBasicRouter(), ActivityResultRouter<D> {

        private val resultCallbackCache = SparseArray<(resultCode: Int, data: D?, e: Exception?) -> Unit>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        override fun startActivityForResult(intent: Intent, options: Bundle?, onResult: (resultCode: Int, data: D?, e: Exception?) -> Unit) {
            try {
                val requestCode = generateRequestCode()
                startActivityForResult(intent, requestCode, options)
                resultCallbackCache.put(requestCode, onResult)
            } catch (e: Exception) {
                onResult(Activity.RESULT_CANCELED, null, e)
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            val callback = resultCallbackCache[requestCode]
            resultCallbackCache.remove(requestCode)
            try {
                callback?.invoke(resultCode, data?.let { getResultData<D>(it) }, null)
            } catch (e: ClassCastException) {
                callback?.invoke(resultCode, null, null)
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            resultCallbackCache.clear()
        }

        /**
         * 生成一个code。
         */
        private fun generateRequestCode(): Int {
            val code = randomGenerator.nextInt(0, 0x0001_0000)
            return if (resultCallbackCache.indexOfKey(code) < 0) {
                code
            } else {
                generateRequestCode()
            }
        }
    }
}