package com.example.smarthome

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class APIController (val context: Context){
    private val baseURL = "http://192.168.1.8:3000"
    fun jsonObjectGET(path: String, completeHandler: (JSONObject?) -> Unit ){
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, baseURL + path, null,
            {response ->
                completeHandler(response)
            },
            { error ->
                VolleyLog.e("/get request fail! Error : ${error.message}")
                completeHandler(null)
            })

        SingletonVolley.getInstance(context.applicationContext).addToRequestQueue(jsonObjectRequest)
    }

    fun jsonObjectPOST(path: String, param: JSONObject,completeHandler: (JSONObject?) -> Unit ){
        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, baseURL + path, param,
            {response ->
                completeHandler(response)
            },
            { error ->
                VolleyLog.e("/post request fail! Error : ${error.message}")
                completeHandler(null)
            }){
            override fun getHeaders(): HashMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Content-Type", "application/json; charset=utf-8")
                headers.put("User-agent", "My useragent");
                return headers
            }

//            override fun getParams(): MutableMap<String, String> {
//                val params = HashMap<String, String>()
//                params.put("params", param.toString())
//                return params
//            }
        }

        SingletonVolley.getInstance(context.applicationContext).addToRequestQueue(jsonObjectRequest)
    }
}