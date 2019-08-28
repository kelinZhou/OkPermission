# OkPermission
#### 实现Android6.0动态权限的优雅获取，一行代码即可实现动态权限的获取。

* * *

## 简介
本库是使用Kotlin语音开发的。可以优雅的申请Android6.0中的动态权限。

## 体验
点击下载[DemoApk](https://fir.im/kmbz)

## 下载
###### 第一步：添加 JitPack 仓库到你项目根目录的 gradle 文件中。
```groovy
allprojects {
    repositories {
        //... 省略N行代码
        maven { url 'https://jitpack.io' }
    }
}
```
###### 第二步：添加这个依赖。
```groovy
dependencies {
    implementation 'com.github.kelinZhou:OkPermission:3.0.2'
}
```
## 使用

#### 一行代码申请动态权限
例如下面获取拨打电话和调用相机的权限。
```kotlin
OkPermission.with(this)
            .addDefaultPermissions(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA
            ).checkAndApply { granted, permissions ->
                if (granted) {
                    Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
                }
            }
```

当用户拒绝权限且不再提醒后会引导用户前往设置页面。引导时会有弹窗，而这个弹窗是可以自定义的。
如果要自定义可以向下面这样。
```kotlin
OkPermission.with(this)
            .addDefaultPermissions(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA
            )
            .interceptMissingPermissionDialog {
                AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("帮助")
                    .setMessage("缺少拨打电话和拍照权限，请前往设置。")
                    .setNegativeButton("退出") { _, _ ->
                        it.continueWorking(false)
                    }
                    .setPositiveButton("设置") { _, _ ->
                        it.continueWorking(true)
                    }.show()
            }.checkAndApply { granted, permissions ->
                if (permissions.isEmpty()) {
                    Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
                }
            }
```

支持APK安装权限，且已适配我手头上现有的手机。
```kotlin
OkPermission.with(this)
            .addDefaultPermissions(Manifest.permission.REQUEST_INSTALL_PACKAGES)
            .checkAndApply { granted, permissions ->
                if (granted) {
                    Toast.makeText(this, "可以安装APK了", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "没有安装APK权限", Toast.LENGTH_SHORT).show()
                }
            }
```

支持通知权限且支持Android8.0中Channel且支持多个Channel。
```kotlin
OkPermission.with(this)
            .addNotificationPermission(false, "Channel1", "Channel2", "Channel3"/*ChannelN ……*/)
            .checkAndApply { granted, permissions ->
                if (granted) {
                    Toast.makeText(this, "用户已同意了全部的通知权限", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "用户拒绝了通知权限", Toast.LENGTH_SHORT).show()
                }
            }
```

支持悬浮窗权限。
```kotlin
OkPermission.with(this)
            .addDefaultPermissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
            .checkAndApply { granted, permissions ->
                if (granted) {
                    Toast.makeText(this, "悬浮窗权限已开启", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "悬浮窗权限已禁用", Toast.LENGTH_SHORT).show()
                }
            }
```

支持检测某些权限是否已开启。
```kotlin
val granted = OkPermission.with(this)
    .addDefaultPermissions(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.CAMERA,
        Manifest.permission.REQUEST_INSTALL_PACKAGES
    ).check()
    .isEmpty()

if (granted) {
    Toast.makeText(this, "可以使用全部的权限", Toast.LENGTH_SHORT).show()
}
```

支持跳转到指定的权限设置页面，且已适配目前手头上的全部手机。
```kotlin
//跳转到权限设置页面，如果跳转失败则会跳转到应用详情页。
OkPermission.gotoPermissionSettingPage(this)
//跳转到Apk安装权限设置页面，需要判断当前SDK版本。
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    OkPermission.gotoInstallPermissionPage(this)
} else {
    Toast.makeText(this, "当前系统版本过低", Toast.LENGTH_SHORT).show()
}
//跳转到通知权限设置页面，可以指定ChannelId，如果指定这跳转到指定Channel的通知设置页面，如果不指定这跳转到通知的总开关设置页面。
OkPermission.gotoNotificationPermissionPage(this/*, "ChannelId"*/)
//跳转到悬浮窗权限设置页面。
OkPermission.gotoSystemWindowPermissionPage(this)
```
## 说明与求助
所有设置页面的跳转均已适配我手头上现有的手机，如果你发现了未适配的手机可以通过自定义```SettingIntentGenerator```类来实现，并请求你将适配代码
也就是你自定义的```SettingIntentGenerator```类贴到Issues里面或发送到我邮箱kelin410@163.com，万分感谢。

下面是自定义```SettingIntentGenerator```的用法。
```kotlin
OkPermission.with(this)
        .addDefaultPermissions(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA
        ).interceptSettingIntentGenerator { permission ->
            if (isMiUi10) {//先判读是不是自己想要拦截的系统
                MiUi10SettingIntentGenerator() //返回你的适配方案。
            } else {
                null
            }
        }
        .checkAndApply { granted, permissions ->
            if (granted) {
                Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
            }
        }
```
* * *
**如果你觉得该库对你有用，欢迎给和Star。如果你有新的适配代码也感谢提供(提交Issues或发邮件给我都行)。感谢！！！**
