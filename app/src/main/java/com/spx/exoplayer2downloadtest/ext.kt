package com.spx.exoplayer2downloadtest

import android.Manifest
import android.content.Context
import android.widget.Toast
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem

fun Context.showToast(content: String): Toast {
    val toast = Toast.makeText(DemoApplication.application, content, Toast.LENGTH_LONG)
    toast.show()
    return toast
}

/**
 * 6.0以下版本(系统自动申请) 不会弹框
 * 有些厂商修改了6.0系统申请机制，他们修改成系统自动申请权限了
 */
fun Context.checkPermission() {
    val permissionItems = ArrayList<PermissionItem>()
//        permissionItems.add(PermissionItem(Manifest.permission.READ_PHONE_STATE, "手机状态", R.drawable.permission_ic_phone))
    permissionItems.add(PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储空间", R.drawable.permission_ic_storage))
    HiPermission.create(this)
            .title("写入外部存储权限申请")
            .msg("为了能够正常使用，请开启这些权限吧！")
            .permissions(permissionItems)
            .style(R.style.PermissionDefaultBlueStyle)
            .animStyle(R.style.PermissionAnimScale)
            .checkMutiPermission(object : PermissionCallback {
                override fun onClose() {
                    showToast("用户关闭了权限")
                }

                override fun onFinish() {
                }

                override fun onDeny(permission: String, position: Int) {
                }

                override fun onGuarantee(permission: String, position: Int) {
                    showToast("权限申请完成")
                }
            })
}