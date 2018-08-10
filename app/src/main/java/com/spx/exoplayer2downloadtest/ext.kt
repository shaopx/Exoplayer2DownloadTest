package com.spx.exoplayer2downloadtest

import android.Manifest
import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

fun Context.showToast(content: String): Toast {
    val toast = Toast.makeText(DemoApplication.application, content, Toast.LENGTH_LONG)
    toast.show()
    return toast
}

abstract class PermissionCallbackAdapter: OnPermission {
    override fun noPermission(denied: MutableList<String>?, quick: Boolean) {

    }

    override fun hasPermission(granted: MutableList<String>?, isAll: Boolean) {
    }

}

fun Context.checkPermission(activity: Activity, callback: OnPermission) {
    XXPermissions.with(activity)
//.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
//.permission(Permission.REQUEST_INSTALL_PACKAGES, Permission.SYSTEM_ALERT_WINDOW) //支持请求安装权限和悬浮窗权限
            .permission(Permission.Group.STORAGE) //支持多个权限组进行请求，不指定则默以清单文件中的危险权限进行请求
            .request(callback)
}
