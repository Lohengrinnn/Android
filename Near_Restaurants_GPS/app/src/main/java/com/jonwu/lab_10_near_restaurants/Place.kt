package com.jonwu.lab_10_near_restaurants

import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject

class Place {
    var id: String?= null
    var icon: String?= null
    var name: String?= null
    var vicinity: String?= null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    companion object {
        internal fun jsonToPointReference(pointRef: JSONObject): Place? {
            try {
                val result = Place()
                val geometry = pointRef.get("geometry") as JSONObject
                var location = geometry.get("location") as JSONObject
                result.latitude = location.get("lat") as Double
                result.longitude = location.get("lng") as Double
                result.icon = pointRef.getString("icon")
                result.name = pointRef.getString("name")
                result.vicinity = pointRef.getString("vicinity")
                result.id = pointRef.getString("id")
                return result
            } catch (ex: JSONException) {

            }
            return null
        }

    }
}