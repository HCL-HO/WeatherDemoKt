/*
 *   Created by Eric Ho on 12/3/20 7:54 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/3/20 2:22 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.service

import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

class ForegroundOnlyLocationService(
    private val context: Context,
    private val locationListener: LocationListener
) {

    var onCompleteListener: OnCompleteListener? = null

    private val LOCATION_VALID_UNTIL: Int = 5 * 60 * 1000

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationListener.onLocation(locationResult?.lastLocation)
            removeCallback()
        }
    }

    private val TAG: String = "ForegroundOnlyLocationService"

    private var fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationRequest: LocationRequest = LocationRequest().apply {
        // Sets the desired interval for active location updates. This interval is inexact. You
        // may not receive updates at all if no location sources are available, or you may
        // receive them less frequently than requested. You may also receive updates more
        // frequently than requested if other applications are requesting location at a more
        // frequent interval.
        //
        // IMPORTANT NOTE: Apps running on Android 8.0 and higher devices (regardless of
        // targetSdkVersion) may receive updates less frequently than this interval when the app
        // is no longer in the foreground.
        interval = TimeUnit.SECONDS.toMillis(1)

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates more frequently than this value.
        fastestInterval = TimeUnit.SECONDS.toMillis(1)

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        maxWaitTime = TimeUnit.MINUTES.toMillis(2)

        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var isLocked = false


    fun getLocation() {
        if (isLocked) {
            Log.d("getLocation", "waiting for previous listener")
            return
        }
        try {
            isLocked = true
            //Get from last GPS within 5 minute
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                isLocked = false
                if (it.result != null && it.result.time > System.currentTimeMillis() - LOCATION_VALID_UNTIL) {
                    locationListener.onLocation(it.result)
                } else {
                    removeCallback()
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest, locationCallback, null
                    )
                }
                onCompleteListener?.onComplete()
            }

        } catch (unlikely: SecurityException) {
            isLocked = false
            onCompleteListener?.onComplete()
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    fun removeCallback() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    interface LocationListener {
        fun onLocation(location: Location?)
    }

    interface OnCompleteListener {
        fun onComplete()
    }

}