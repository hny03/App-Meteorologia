package com.example.climateapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class LocationService(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 10000 // 10 seconds
        fastestInterval = 5000 // 5 seconds
    }

    fun getLastLocation(onLocationReceived: (Location?) -> Unit) {
        Log.d("LocationService", "Requesting location")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("LocationService", "Location permission not granted")
            onLocationReceived(null)
            return
        }

        // First try to get the last known location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("LocationService", "Last known location received: $location")
                    onLocationReceived(location)
                } else {
                    Log.d("LocationService", "No last known location, requesting updates")
                    // If no last known location, request location updates
                    requestLocationUpdates(onLocationReceived)
                }
            }
            .addOnFailureListener { e ->
                Log.e("LocationService", "Error getting last location", e)
                // If getting last location fails, request location updates
                requestLocationUpdates(onLocationReceived)
            }
    }

    private fun requestLocationUpdates(onLocationReceived: (Location?) -> Unit) {
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

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        Log.d("LocationService", "Location update received: $location")
                        onLocationReceived(location)
                        // Stop updates after getting the first location
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            },
            null
        )
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        Log.d("LocationService", "Location services enabled: $isEnabled")
        return isEnabled
    }
} 