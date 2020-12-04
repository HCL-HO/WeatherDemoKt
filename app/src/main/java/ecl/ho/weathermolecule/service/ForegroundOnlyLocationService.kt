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

    private val LOCATION_VALID_UNTIL: Int = 5 * 60 * 1000

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationListener.onLocation(locationResult?.lastLocation)
            removeCallback()
        }
    }

    private val TAG: String = "ForegroundOnlyLocationService"

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest


    fun getLocation() {
        // TODO: Step 1.2, Review the FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        // TODO: Step 1.3, Create a LocationRequest.
        locationRequest = LocationRequest().apply {
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

        try {
            //Get from last GPS within 5 minute
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                if (it.result.time > System.currentTimeMillis() - LOCATION_VALID_UNTIL) {
                    locationListener.onLocation(it.result)
                } else {
                    removeCallback()
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest, locationCallback, Looper.myLooper()
                    )
                }
            }

        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    fun removeCallback() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    interface LocationListener {
        fun onLocation(location: Location?)
    }

}