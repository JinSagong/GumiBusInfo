package com.jin.businfo_gumi.widget

import android.content.Context
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.jin.businfo_gumi.util.Debug

@Suppress("UNUSED")
object PermissionDialog {
    fun with(context: Context) = PermissionDialog(context)

    class PermissionDialog(context: Context) {
        private val tedPermission = TedPermission.with(context)
        private var grantedListener: (() -> Unit)? = null
        private var deniedListener: (() -> Unit)? = null

        fun setPermission(vararg permissions: String) = apply {
            tedPermission.setPermissions(*permissions)
        }

        fun doOnGranted(callback: () -> Unit) = apply { grantedListener = callback }
        fun doOnDenied(callback: () -> Unit) = apply { deniedListener = callback }

        fun check() {
            tedPermission.setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    grantedListener?.invoke()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Debug.e("[Permission Denied] permission=${deniedPermissions.toString()}")
                    deniedListener?.invoke()
                }
            }).check()
        }
    }
}