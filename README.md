# OkPermission
#### 实现Android6.0动态权限的优雅获取，一行代码即可实现动态权限的获取。

* * *

## 简介
本库是使用Kotlin语音开发的。可以优雅的申请Android6.0中的动态权限。

## 下载
###### 第一步：添加 JitPack 仓库到你项目根目录的 gradle 文件中。
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
###### 第二步：添加这个依赖。
```
dependencies {
    implementation 'com.github.kelinZhou:Okpermission:1.0.0'
}
```

## 使用
#### 第一步：在Application中初始化
```
OkPermission.init(this)
```
#### 第二步：一行代码申请动态权限
例如下面获取拨打电话和调用相机的权限。
```
OkPermission.instance.applyPermissions(Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA) { permissions ->
    if (permissions.isEmpty()) {
        Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
    }
}
```

你也可以选择在用户拒绝权限后向用户做出解释，你可以向下面这样做。
```
//R.string.request_permission_explain就是你需要向用户做出申请权限的解释的内容。
OkPermission.instance.applyPermissions(R.string.request_permission_explain, Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA) { permissions ->
    if (permissions.isEmpty()) {
        Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
    }
}
```

**如果你觉得该库对你有用，欢迎给和Star。感谢！！！**
