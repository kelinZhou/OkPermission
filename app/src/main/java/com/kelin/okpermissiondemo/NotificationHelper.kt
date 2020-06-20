package com.kelin.okpermissiondemo

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import android.app.Notification.PRIORITY_DEFAULT
import android.app.Notification.VISIBILITY_SECRET
import androidx.annotation.StringRes

/**
 * **描述:** 系统通知工具。
 *
 *
 * **创建人:** kelin
 *
 *
 * **创建时间:** 2019-07-30  18:04
 *
 *
 * **版本:** v 1.0.0
 */
class NotificationHelper private constructor(base: Context) : ContextWrapper(base) {

    private val manager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    private val currentChannels = ArrayList<String>()

    private val defaultChannel: String
        get() = if (currentChannels.isEmpty()) {
            ""
        } else {
            currentChannels[0]
        }

    private fun init(channels: Array<out String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channels)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channels: Array<out String>) {
        if (channels.isNotEmpty()) {
            currentChannels.clear()
            currentChannels.addAll(channels)
            val channelList = ArrayList<NotificationChannel>(channels.size)
            for (c in channels) {
                val channel = NotificationChannel(
                    c,
                    c,
                    NotificationManager.IMPORTANCE_HIGH
                )
                //是否绕过请勿打扰模式
                channel.canBypassDnd()
                //闪光灯
                channel.enableLights(true)
                //锁屏显示通知
                channel.lockscreenVisibility = VISIBILITY_SECRET
                //闪关灯的灯光颜色
                channel.lightColor = Color.RED
                //桌面launcher的消息角标
                channel.canShowBadge()
                //是否允许震动
                channel.enableVibration(true)
                //获取系统通知响铃声音的配置
                channel.audioAttributes
                //获取通知取到组
                channel.group
                //设置可绕过  请勿打扰模式
                channel.setBypassDnd(true)
                //设置震动模式
                channel.vibrationPattern = longArrayOf(100, 100, 200)
                //是否会有灯光
                channel.shouldShowLights()
                channelList.add(channel)
            }
            manager.createNotificationChannels(channelList)
        }
    }

    private fun getNotification(title: String, content: String, channel: String): NotificationCompat.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(applicationContext, channel).apply {
                priority = NotificationManager.IMPORTANCE_HIGH
            }
        } else {
            NotificationCompat.Builder(applicationContext).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    priority = NotificationManager.IMPORTANCE_HIGH
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    priority = Notification.PRIORITY_HIGH
                }
            }
        }.setSmallIcon(R.mipmap.ic_launcher)//小图标
            .setContentTitle(title)//标题
            .setContentText(content)//文本内容
            .setStyle(NotificationCompat.BigTextStyle())//样式
            .setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS)
            .setVibrate(longArrayOf(100, 200, 300, 400))
            .setOngoing(false)//设置可以滑动删除
            .setAutoCancel(true)//设置点击信息后自动清除通知
    }

    /**
     * 发送通知
     */
    fun sendNotification(title: String, content: String, channel: String = defaultChannel) {
        val builder = getNotification(title, content, channel)
        manager.notify(1, builder.build())
    }

    /**
     * 发送通知
     */
    fun sendNotification(notifyId: Int, title: String, content: String, channel: String = defaultChannel) {
        val builder = getNotification(title, content, channel)
        manager.notify(notifyId, builder.build())
    }

    /**
     * 发送带有进度的通知
     */
    fun sendNotificationProgress(title: String, content: String, progress: Int, intent: PendingIntent, channel: String = defaultChannel) {
        val builder = getNotificationProgress(title, content, progress, intent, channel)
        manager.notify(0, builder.build())
    }

    /**
     * 获取带有进度的Notification
     */
    private fun getNotificationProgress(
        title: String,
        content: String,
        progress: Int,
        intent: PendingIntent,
        channel: String
    ): NotificationCompat.Builder {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(applicationContext, channel)
        } else {
            NotificationCompat.Builder(applicationContext).apply {
                priority = PRIORITY_DEFAULT
            }
        }
        //标题
        builder.setContentTitle(title)
        //文本内容
        builder.setContentText(content)
        //小图标
        builder.setSmallIcon(R.mipmap.ic_launcher)
        //设置大图标，未设置时使用小图标代替，拉下通知栏显示的那个图标
        //设置大图片 BitmpFactory.decodeResource(Resource res,int id) 根据给定的资源Id解析成位图
        builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        if (progress in 1..99) {
            //一种是有进度刻度的（false）,一种是循环流动的（true）
            //设置为false，表示刻度，设置为true，表示流动
            builder.setProgress(100, progress, false)
        } else {
            //0,0,false,可以将进度条隐藏
            builder.setProgress(0, 0, false)
            builder.setContentText("下载完成")
        }
        //设置点击信息后自动清除通知
        builder.setAutoCancel(true)
        //通知的时间
        builder.setWhen(System.currentTimeMillis())
        //设置点击信息后的跳转（意图）
        builder.setContentIntent(intent)
        return builder
    }

    companion object {
        private var applicationContext: Context? = null
        val instance: NotificationHelper by lazy {
            val context = applicationContext
            if (context != null) {
                applicationContext = null
                NotificationHelper(context)
            } else {
                throw NullPointerException("you need call the init method!")
            }
        }

        fun init(context: Context, vararg channels: String) {
            applicationContext = context.applicationContext
            instance.init(channels)
        }

        fun init(context: Context, @StringRes vararg channels: Int) {
            applicationContext = context.applicationContext
            instance.init(channels.map { context.resources.getString(it) }.toTypedArray())
        }
    }
}
