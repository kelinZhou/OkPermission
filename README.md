# OkPermission
#### 实现Android6.0动态权限的优雅获取，一行代码即可实现动态权限的获取。

* * *

## 简介
本库是使用Kotlin语音开发的。可以优雅的申请Android6.0中的动态权限。

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
    implementation 'com.github.kelinZhou:OkPermission:2.0.0'
}
```

## 使用

#### 一行代码申请动态权限
例如下面获取拨打电话和调用相机的权限。
```kotlin
OkPermission.with(this)
            .applyPermissions(Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA){ permissions ->
                if (permissions.isEmpty()) {
                    Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
                }
            }
```

你也可以选择在用户拒绝权限后向用户做出解释，你可以像下面这样做。
```kotlin
OkPermission.with(this, "为了更好的服务于您，请允许我们需要的权限。")
            .applyPermissions(Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA){ permissions ->
                if (permissions.isEmpty()) {
                    Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
                }
            }
```

如果你要申请的权限都是必要权限，你想要暴力的让用户同意你所有的权限可以像下面这样做。
```kotlin
OkPermission.with(this, "为了更好的服务于您，请允许我们需要的权限。")
            .forceApplyPermissions(Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA){ permissions ->
                if (permissions.isEmpty()) {
                    Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
                }
            }
```
这样使用的话会一直提示用户授予权限，直到用户授予了全部权限或点击了不在询问后再回停止提示用户。



如果在你想要申请的所有权限中，有些权限是必须的而有些权限是非必须的，你可以像下面这样做。
```kotlin
OkPermission.with(this, getString(R.string.request_permission_explain)).mixApplyPermissions(
                Permission.create(Manifest.permission.CALL_PHONE, true),
                Permission.create(Manifest.permission.CAMERA, false)
            ) { granted, permissions ->
                when {
                    permissions.isEmpty() -> Toast.makeText(this, "所有权限已获取", Toast.LENGTH_SHORT).show()
                    granted -> Toast.makeText(this, "所有必须的权限已获取", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
                }
            }
```
* * *
**如果你觉得该库对你有用，欢迎给和Star。感谢！！！**
