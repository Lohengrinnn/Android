package com.jonwu.lab_10_near_restaurants

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Result : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var GMap: GoogleMap
    var locationManager : LocationManager?= null
    var locationListener : LocationListener?= null


    override fun onMapReady(googleMap: GoogleMap?) {
        GMap = googleMap!!
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object: LocationListener {
            override fun onLocationChanged(location: Location?) {
                if (location != null) {
                    val userLocation = LatLng(location!!.latitude, location!!.longitude)
                    GMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                    GMap.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }

            override fun onProviderEnabled(provider: String?) {

            }

            override fun onProviderDisabled(provider: String?) {

            }

        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1f, locationListener)
        }

        var currentLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        var getPlaces = GetPlaces(this, currentLocation)
        getPlaces.execute()
    }

    internal inner class GetPlaces(var context: Context, location: Location?) :
        AsyncTask<Void, Void, Void>() {
        var restaurants: ArrayList<Place?>? = null
        var currentLocation: Location?= location

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            setMarkers(GMap, restaurants)
        }

        override fun doInBackground(vararg arg0: Void): Void? {
            restaurants = getRestaurantsFromGoogle(GMap, currentLocation)
            return null
        }

        fun getRestaurantsFromGoogle(map: GoogleMap, location: Location?):
                ArrayList<Place?>? {
            val service = PlacesService("AIzaSyDZb-4UIdq5KpG9YiV5HFTuUSIi24_6MH4")
            var restaurants: ArrayList<Place?>? = service.findRestaurants(location!!.latitude, location!!.longitude)
            Log.e("getRestaurant","${location!!.latitude}, ${location!!.longitude}: ${restaurants?.size}")
            return restaurants
        }

        private fun getMarkerFromResult(result: Place?): MarkerOptions {
            val mo = MarkerOptions()
            mo.position(LatLng(result!!.latitude, result!!.longitude))
                .title(result!!.name)
                .snippet(result!!.vicinity)
                .infoWindowAnchor(0.5f, 0.5f)
            return mo
        }

        private fun setMarkers(map: GoogleMap, markers: ArrayList<Place?>?) {
            for (place in markers!!) {
                map.addMarker(getMarkerFromResult(place))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
}
