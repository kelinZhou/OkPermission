package com.kelin.okpermissiondemo

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kelin.okpermission.OkPermission
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_more_opration, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuGoToAppDetail -> {
                OkPermission.gotoPermissionSettingPage(this)
                true
            }

            R.id.menuGoToApkInstall -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    OkPermission.gotoInstallPermissionPage(this)
                } else {
                    Toast.makeText(this, "当前系统版本过低", Toast.LENGTH_SHORT).show()
                }
                true
            }

            R.id.menuGoToNotification -> {
                OkPermission.gotoNotificationPermissionPage(this)
                true
            }

            R.id.menuGoToSystemWindow -> {
                OkPermission.gotoSystemWindowPermissionPage(this)
                true
            }

            R.id.menuGoToModifySettings -> {
                OkPermission.gotoWriteSettingsPage(this)
                true
            }

            R.id.menuGoToGPSSettings -> {
                OkPermission.gotoGPSSettingsPage(this)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv1.setOnClickListener {
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
        }

//        tv2.setOnClickListener {
//            OkPermission.with(this).addDefaultPermissions(
//                Manifest.permission.CALL_PHONE,
//                Manifest.permission.CAMERA
//            ).checkAndApply { granted, _ ->
//                if (granted) {
//                    Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

        tv3.setOnClickListener {
            OkPermission.with(this)
                .addForcePermissions(
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.CAMERA
                ).checkAndApply { granted, permissions ->
                    if (granted) {
                        Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        tv4.setOnClickListener {
            OkPermission.with(this)
                .addWeakPermissions(Manifest.permission.CAMERA)
                .addDefaultPermissions(Manifest.permission.CALL_PHONE)
                .addForcePermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .checkAndApply { granted, permissions ->
                    when {
                        permissions.isEmpty() -> Toast.makeText(this, "所有权限已获取", Toast.LENGTH_SHORT).show()
                        granted -> Toast.makeText(this, "所有必须的权限已获取", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        tv5.setOnClickListener {
            OkPermission.with(this)
                .addDefaultPermissions(
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.REQUEST_INSTALL_PACKAGES
                )
                .addNotificationPermission(false, "系统消息")
                .checkAndApply { granted, permissions ->
                    if (granted) {
                        Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
                    } else if (!permissions.contains(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
                        Toast.makeText(this, "安装权限已获取", Toast.LENGTH_SHORT).show()
                    } else if (!permissions.contains(OkPermission.permission.NOTIFICATION)) {
                        Toast.makeText(this, "通知权限已获取", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        tv6.setOnClickListener {
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
        }

        tv7.setOnClickListener {
            OkPermission.with(this)
                .addDefaultPermissions(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                .checkAndApply { granted, permissions ->
                    if (granted) {
                        Toast.makeText(this, "可以安装APK了", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "没有安装APK权限", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        tv8.setOnClickListener {
            OkPermission.with(this)
                .addNotificationPermission(false, "系统消息")
                .checkAndApply { granted, permissions ->
                    if (granted) {
                        NotificationHelper.instance.sendNotification(2, "权限变更", "用户已授权通知权限")
                    } else {
                        Toast.makeText(this, "用户拒绝了通知权限", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        tv9.setOnClickListener {
            OkPermission.with(this)
                .addDefaultPermissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .checkAndApply { granted, permissions ->
                    if (granted) {
                        Toast.makeText(this, "悬浮窗权限已开启", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "悬浮窗权限已禁用", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        tv10.setOnClickListener {
            OkPermission.with(this)
                .addDefaultPermissions(Manifest.permission.WRITE_SETTINGS)
                .checkAndApply { granted, permissions ->
                    if (granted) {
                        Toast.makeText(this, "系统设置权限已开启", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "系统设置权限已禁用", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        tv11.setOnClickListener {
            if (OkPermission.with(this).addDefaultPermissions(OkPermission.permission.GPS).isGranted()) {
                OkPermission.with(this)
                    .addDefaultPermissions(*OkPermission.permission_group.ACCESS_LOCATION)
                    .checkAndApply { granted, permissions ->
                        if (granted) {
                            Toast.makeText(this, "定位权限已开启", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "定位权限已禁用", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                AlertDialog.Builder(this)
                    .setTitle("提示：")
                    .setMessage("需要打开系统定位开关，用于定位服务。")
                    .setNegativeButton("暂不开启") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("去设置") { dialog, which ->
                        dialog.dismiss()
                        OkPermission.with(this)
                            .addDefaultPermissions(OkPermission.permission.GPS)
                            .checkAndApplyOnly()
                    }.show()
            }
        }
    }
}
