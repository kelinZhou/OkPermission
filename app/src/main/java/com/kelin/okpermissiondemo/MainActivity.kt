package com.kelin.okpermissiondemo

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kelin.okpermission.OkPermission
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv1.setOnClickListener {
            OkPermission.instance.applyPermissions(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA
            ) { permissions ->
                if (permissions.isEmpty()) {
                    Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tv2.setOnClickListener {
            OkPermission.instance.applyPermissions(
                R.string.request_permission_explain,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA
            ) { permissions ->
                if (permissions.isEmpty()) {
                    Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tv3.setOnClickListener {
            OkPermission.instance.forceApplyPermissions(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA
            ) { permissions ->
                if (permissions.isEmpty()) {
                    Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tv4.setOnClickListener {
            OkPermission.instance.forceApplyPermissions(
                R.string.request_permission_explain,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA
            ) { permissions ->
                if (permissions.isEmpty()) {
                    Toast.makeText(this, "权限已全部获取", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "未获取全部权限", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
