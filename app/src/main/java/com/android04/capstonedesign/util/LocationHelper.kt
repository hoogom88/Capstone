package com.android04.capstonedesign.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// 위치 정보 수집기

class LocationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var updateInterval = 0L
    private val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    lateinit var updateCallback: (Double, Double) -> (Unit)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult.equals(null)) {
                return
            }
            for (location in locationResult.locations) {
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    updateCallback(latitude, longitude)
                    Log.i(
                        TAG,
                        "Logging;GPS Location changed, Latitude: $latitude, Longitude: $longitude"
                    )
                }
            }
        }
    }

    fun requestLocationUpdate(interval: Long, callback: (Double, Double) -> (Unit)) {
        this.updateInterval = interval
        updateCallback = callback
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = updateInterval
        locationRequest.fastestInterval = updateInterval
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            Log.i(TAG, "location client setting success")
        }
        task.addOnFailureListener {
            Log.i(TAG, "location client setting failure")
        }
    }

    fun removeLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
    
    companion object {
        const val TAG = "LocationHelperLog"
    }


}