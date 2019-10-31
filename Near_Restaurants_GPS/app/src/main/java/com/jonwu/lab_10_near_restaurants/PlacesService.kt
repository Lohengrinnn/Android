package com.jonwu.lab_10_near_restaurants

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL
import java.util.logging.Level
import java.util.logging.Logger

class PlacesService(private var API_KEY: String?) {
    fun setApiKey(apikey: String) {
        this.API_KEY = apikey
    }

    fun findRestaurants(latitude: Double, longitude: Double): ArrayList<Place?> {
        val urlString = makeUrl(latitude, longitude, "restaurant")
        println(urlString)

        val arrayList = ArrayList<Place?>()
        try {
            val json = getJSON(urlString)

            Log.e("findRestaurants", json)
            val `object` = JSONObject(json)
            val array = `object`.getJSONArray("results")

            for (i in 0 until array.length()) {
                try {
                    var place: Place? = Place.jsonToPointReference(array.get(i) as JSONObject)
                    arrayList.add(place)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return arrayList
        } catch (ex: JSONException) {
            Logger.getLogger(PlacesService::class.java.name).log(Level.SEVERE, null, ex)

            Log.e("findRestaurants", "2")
        }
        return arrayList
    }

    private fun makeUrl(latitude: Double, longitude: Double, place: String): String {
        val urlString = StringBuilder("https://maps.googleapis.com/maps/api/place/search/json?")

        if (place == "") {
            urlString.append("&location=")
            urlString.append(java.lang.Double.toString(latitude))
            urlString.append(",")
            urlString.append(java.lang.Double.toString(longitude))
            urlString.append("&radius=1000")
            urlString.append("&sensor=false&key=" + API_KEY!!)
        } else {
            urlString.append("&location=")
            urlString.append(java.lang.Double.toString(latitude))
            urlString.append(",")
            urlString.append(java.lang.Double.toString(longitude))
            urlString.append("&radius=1000")
            urlString.append("&types=" + place)
            urlString.append("&sensor=false&key=" + API_KEY!!)
        }

        return urlString.toString()
    }

    protected fun getJSON(url: String): String {
        return getUrlContents(url)
    }

    private fun getUrlContents(theUrl: String): String {
        val content = StringBuilder()
        try {
            val url = URL(theUrl)
            val urlConnection = url.openConnection()
            val bufferedReader = BufferedReader(InputStreamReader(urlConnection.getInputStream()))
            var line: String?
            do {
                line = bufferedReader.readLine()
                content.append(line + "\n")
            } while (line != null)
            bufferedReader.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("getUrlContents", "1")
        }
        return content.toString()
    }
}