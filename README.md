# OkPermission
#### 实现Android6.0动态权限的优雅获取，一行代码即可实现动态权限的获取。

* * *

## 简介
本库是使用Kotlin语音开发的。可以优雅的申请Android6.0中的动态权限。

## 体验
[点击下载](https://fir.im/kmbz)或扫码下载DemoApk

![DemoApk](materials/demo_download.png)

## 更新
#### 3.1.0 迁移至Androidx并优化代码增加易用性。
1.从3.1.0开始，全面迁移至Androidx，如果您的项目还没有迁移至Androidx请使用3.0.4或之前的版本。 

2.更加好用的OkActivityResult,result回调不再返回Intent，而是直接返回通过泛型指定数据类型。
  您需要在要返回结果的页面调用OkActivityResult.setResultData(activity, data)方法为调起页面返回数据(推荐)，
  或则使用OkActivityResult.KEY_RESULT_DATA作为键为Intent塞值。

3.为OkActivityResult的startActivity方法和startActivityOrException方法分别增加不需要携带数据的回调重
  载(如果你只需要关心ResultCode不需要带回任何数据可以使用，这样你就无需指定泛型，简化代码书写)。
#### 3.0.4
1.为with方法增加一个参数为Context的重载，将是否为Activity的判断进行内聚。

2.OkActivityResult启动Activity时支持options参数，且无需做版本判断。

3.OkActivityResult增加一系列setResult方法以及getResultData方法，使其更加易用。

4.启动Activity后的回调函数中默认不再有Exception，并将方法名更改为"startActivity"和“startActivityOrException”。
#### 3.0.3
修复8.0以下申请通知权限时如果设置了Channel的话会导致多次打开通知权限设置页面的bug。
#### 3.0.2
优化通知权限的申请，将检查权限由回调的方式改为返回的方式。
#### 3.0.1
对外暴露检测权限的接口。
#### 3.0.0
适配悬浮窗权限、通知权限，框架重构，扩展性强。
#### 2.0.4
适配Android8.0的Apk安装权限。
#### 2.0.2
增加自定义弹窗的接口，支持自定义弹窗。
#### 2.0.1
优化权限申请流程。


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
    implementation 'com.github.kelinZhou:OkPermission:3.0.4'
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

## OkActivityResult
为了监听某个权限设置页面用户操作后(执行onActivityResult方法时)重新校验权限是否已经开启，所以内部封装了一个具有完善的功能的处理onActivityResult组件，
核心类为OkActivityResult。使用该组件可以通过回调的方式接受到onActivityResult的数据，并且可以直接通过泛型指定数据类型，避免类型强转。

####使用方法
当你需要调起某个目标页面而不需要向目标页面传递任何数据且不需要从目标页面获取数据时。
```kotlin
OkActivityResult.startActivity(this, MainActivity::class.java) { resultCode ->
    if (resultCode == Activity.RESULT_OK) {
        //Do something……
    }
}
```
当你需要调起某个目标页面而不需要向目标页面传递任何数据但需要从目标页面获取数据时。
```kotlin
OkActivityResult.startActivity<Person>(this, PersonSelectorActivity::class.java) { resultCode, person ->
    if (person != null) {
        //Do something……
    }
}
```
如果你需要向目标页面传递一些数据你只需要将```kotlin XXXActivity::class.java```替换成Intent即可。
```kotlin
val intent = Intent(this, PersonSelectorActivit::class.java)
intent.putExtra("age", 18)
OkActivityResult.startActivity(this, intent) { resultCode, person ->
    if (person != null) {
        //Do something……
    }
}
```
如果你不确定你要调起的某个页面是否存在，当不存在时你需要自己处理异常时，你需要调用```kotlin startActivityOrException```方法。
```kotlin
OkActivityResult.startActivityOrException(this, Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))) { resultCode, e ->
    if (e != null) {
        // Handle Activity not found. 
    }else{
        //Do something……
    }
}
```
**补充**你需要在目标页面中调用```kotlin OkActivityResult.setResultData(activity, person)```方法才能在发起页面获取到数据。

* * *
**如果你觉得该库对你有用，欢迎给和Star。如果你有新的适配代码也感谢提供(提交Issues或发邮件给我都行)。感谢！！！**


* * *
### License
```
Copyright 2016 kelin410@163.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```