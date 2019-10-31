package com.jonwu.android_calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import okhttp3.RequestBody





class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var N1 = Number1.text
        var N2 = Number2.text
        button_plus.setOnClickListener {
            try {
                var sum = N1.toString().toDouble() + N2.toString().toDouble()
                result_view.text = sum.toString()
            } catch (e: Exception) {
                result_view.text = "input is invalid"
            }
        }

        button_minus.setOnClickListener{
            try {
                var min = N1.toString().toDouble() - N2.toString().toDouble()
                result_view.text = min.toString()
            } catch (e: Exception) {
                result_view.text = "input is invalid"
            }
        }

        button_multiply.setOnClickListener{
            try {
                var mul = N1.toString().toDouble() * N2.toString().toDouble()
                result_view.text = mul.toString()
            } catch (e: Exception) {
                result_view.text = "input is invalid"
            }
        }

        button_divide.setOnClickListener{
            try {
                var div = N1.toString().toDouble() / N2.toString().toDouble()
                result_view.text = div.toString()
            } catch (e: Exception) {
                result_view.text = "input is invalid"
            }
        }

        button_plus_get.setOnClickListener {
            val client: OkHttpClient = OkHttpClient()

            val request = Request.Builder()
                .url("http://10.0.0.132:4000/api/addition?first_number=${N1.toString()}&second_number=${N2.toString()}")
                .build()

            Log.e("REST", "request addition")
            val call = client.newCall(request)

            /* sync call
            *  WARNING: THIS CODE DOESN'T WORK
            *  this app will crash due to android.os.NetworkOnMainThreadException
            *  if using sync call in the main thread
            *  I guess this exception is for the sake of preventing user interaction
            *  from being blocked by synchronous and time consuming process
            * */
            /* val response: Response = call.execute()
            val s = response.body()!!.string()
            result_view.text = s */

            /* asyn call */
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println(e.message)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        /* Callback works in a non-mainthread, if not embraced by runOnUiThread,
                        *  CalledFromWrongThreadException will be arised: Only the original thread
                        *  that created a view hierarchy can touch its views.
                        */
                        runOnUiThread {
                            val s = response.body()!!.string()
                            result_view.text = s
                            Log.e("REST", s)
                            Toast.makeText(
                                baseContext,
                                s,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            })
        }

        button_plus_post.setOnClickListener {
            var jsonObject = JSONObject()
            jsonObject.put("first_number", "${N1.toString()}");
            jsonObject.put("second_number", "${N2.toString()}");

            val JSON = MediaType.parse("application/json; charset=utf-8")
            val body = RequestBody.create(JSON, jsonObject.toString())

            val client: OkHttpClient = OkHttpClient()

            /* FormBody, another way is JSON, which is used by code above
            val body: RequestBody = FormBody.Builder()
                .add("first_number", "${N1.toString()}")
                .add("second_number", "${N2.toString()}")
                .build();*/

            val request = Request.Builder()
                .url("http://10.0.0.132:4000/api/addition")
                .post(body)
                .build()

            Log.e("REST", "request addition by post")
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("POST", e.message!!)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        runOnUiThread {
                            val s = response.body()!!.string()
                            result_view.text = s
                            Log.e("REST", s)
                            Toast.makeText(
                                baseContext,
                                s,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            })
        }
    }
}
