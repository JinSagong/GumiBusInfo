package com.jin.businfo_gumi.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.gun0912.tedonactivityresult.TedOnActivityResult
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.widget.MsgBottomSheetDialog
import com.jin.businfo_gumi.widget.PermissionDialog
import com.jin.businfo_gumi.widget.Toasty
import kotlin.math.*

@Suppress("UNUSED")
class LocationUtil(private val activity: Activity, private val distance: Float = DEFAULT_DISTANCE) {
    private val locationClient by lazy { LocationServices.getFusedLocationProviderClient(activity) }
    private val locationManager by lazy { activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private val locationRequest by lazy {
        LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
        }
    }
    var hasRequested = false
        private set

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result?.let {
                val lat = it.lastLocation.latitude.toFloat()
                val lng = it.lastLocation.longitude.toFloat()
                Debug.i("Location(lat:$lat, lng:$lng)")
                trackBoundary(lat, lng)
            }
        }

        override fun onLocationAvailability(availability: LocationAvailability?) {
            if (availability?.isLocationAvailable != true) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // turn on the gps function
                    val builder = LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest)
                    val client = LocationServices.getSettingsClient(activity)
                    client.checkLocationSettings(builder.build())
                        .addOnFailureListener {
                            if (it is ResolvableApiException) {
                                try {
                                    it.startResolutionForResult(activity, REQUEST_LOCATION)
                                } catch (sendEx: IntentSender.SendIntentException) {
                                    Debug.e("sendIntentException:${sendEx.message}")
                                    cancelTracking()
                                }
                            }
                        }
                } else {
                    cancelTracking(flag = false)
                }
            }
        }
    }

    private var onUpdateLocationListener: ((Float, Float, Float) -> Unit)? = null
    private var onFailureListener: ((Boolean) -> Unit)? = null

    fun doOnUpdateLocation(l: (Float, Float, Float) -> Unit) =
        apply { onUpdateLocationListener = l }

    fun doOnFailure(l: (Boolean) -> Unit) = apply { onFailureListener = l }

    fun startTracking() {
        PermissionDialog.with(activity)
            .setPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .doOnGranted { executeTracking() }
            .doOnDenied {
                MsgBottomSheetDialog.withTwoBtn(activity)
                    .setMessage(R.string.toPermissionSetting)
                    .setOnDoneListener {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", activity.packageName, null)
                        intent.data = uri
                        TedOnActivityResult.with(activity)
                            .setIntent(intent)
                            .setListener { _, _ ->
                                val isGranted = ContextCompat.checkSelfPermission(
                                    activity, Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                if (isGranted) executeTracking() else {
                                    onFailureListener?.invoke(true)
                                    Toasty.alert(R.string.noPermission)
                                }
                            }
                            .startActivityForResult()
                    }
                    .setOnCancelListener {
                        onFailureListener?.invoke(true)
                        Toasty.alert(R.string.noPermission)
                    }
                    .show()
            }
            .check()
    }

    @SuppressLint("MissingPermission")
    private fun executeTracking() {
        try {
            hasRequested = true
            locationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper()!!
            )
        } catch (e: Exception) {
            cancelTracking()
        }
    }

    fun cancelTracking(doListener: Boolean = true, flag: Boolean = true) {
        if (hasRequested) {
            if (doListener) onFailureListener?.invoke(flag)
            locationClient.removeLocationUpdates(locationCallback)
        }
        hasRequested = false
    }

    private fun trackBoundary(lat: Float, lng: Float) {
        onUpdateLocationListener?.invoke(lat, lng, getRadius(distance))
    }

    companion object {
        const val REQUEST_LOCATION = 213
        const val RESPONSE_LOCATION_OK = -1
        const val RESPONSE_LOCATION_CANCEL = 0

        private const val DEFAULT_DISTANCE = 2000f // 2km

        fun getDistance(lat1: Double, lng1: Double, lat2: Float, lng2: Float): Double {
            return 1.609344 * 1000 * 60 * 1.1515 * 180 / PI * acos(
                sin(lat1 * PI / 180) * sin(lat2 * PI / 180)
                        + cos(lat1 * PI / 180) * cos(lat2 * PI / 180) * cos((lng2 - lng1) * PI / 180)
            )
        }

        fun getRadius(distance: Float = DEFAULT_DISTANCE) =
            distance / (1.609344f * 1000f * 60f * 1.1515f)

        fun getDistanceText(distance: Double) =
            if (distance < 1000) "${distance.roundToInt()}m"
            else "${(distance / 10).roundToInt().toFloat() / 100}km"
    }
}