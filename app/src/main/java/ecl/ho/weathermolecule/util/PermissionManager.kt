/*
 *   Created by Eric Ho on 12/3/20 7:54 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 11/30/20 8:51 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.os.Build
import android.util.SparseArray
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat


/**
 * Created by eric on 19/1/2018.
 */
class PermissionManager(private val activity: Activity) {
    interface PermissionListener {
        fun onPermissionsGranted()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermission(listener: PermissionListener, vararg permissions: String) {
        val unGrantedPermission: MutableList<String> = ArrayList()
        permissions.forEach {
            if (!permissionGranted(it, activity)) {
                unGrantedPermission.add(it)
            }
        }
        if (unGrantedPermission.size > 0) {
            registerCallback(listener, REQUEST_ALL)
            activity.requestPermissions(
                unGrantedPermission.toTypedArray(),
                REQUEST_ALL
            )
        } else {
            listener.onPermissionsGranted()
        }
    }

    //Register when request permission
    //Unregister / call onPermissionsGranted on request result
    private val listenerSparseArray = SparseArray<PermissionListener?>()
    private fun registerCallback(listener: PermissionListener?, requestCode: Int) {
        listenerSparseArray.put(requestCode, listener)
    }

    fun unregisterListener(requestCode: Int) {
        listenerSparseArray.remove(requestCode)
    }

    fun onPermissionsGranted(requestCode: Int) {
        val listenerInstance = listenerSparseArray[requestCode]
        if (listenerInstance != null) {
            listenerInstance.onPermissionsGranted()
            unregisterListener(requestCode)
        }
    }

    companion object {
        const val REQUEST_ALL = 9
        fun permissionGranted(permission: String, activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun arePermissionGranted(grantResults: IntArray): Boolean {
            for (result in grantResults) {
                if (result == PERMISSION_DENIED) {
                    return false
                }
            }
            return true
        }
    }
}