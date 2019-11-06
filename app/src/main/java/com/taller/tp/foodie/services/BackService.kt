package com.taller.tp.foodie.services

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.taller.tp.foodie.MyApplication
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

const val SERVICE_ARRAY_RESPONSE = "service-array-response"

open class BackService {

    companion object {
        @Volatile
        private var INSTANCE: BackService? = null

        fun getInstance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: BackService().also {
                    INSTANCE = it
                }
            }
    }

    private val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        val appContext = MyApplication.getContext()
        Volley.newRequestQueue(appContext)
    }

    private val url = getUrl()

    private fun getUrl(): String {
        val appContext = MyApplication.getContext()
        val stream = appContext.assets.open("environment.properties")
        val properties = Properties()
        properties.load(stream)
        return properties.getProperty("foodie-back.url")
    }


    fun doGetObject(
        method: String,
        onSuccess: Response.Listener<JSONObject>,
        onError: Response.ErrorListener = Response.ErrorListener { error -> Log.d("Error.Response", error.toString()) }
    ){
        try{
            val finalUrl = url+method
            val getRequest = object : JsonObjectRequest(
                Method.GET,
                finalUrl, null,
                onSuccess,
                onError) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    val authToken = UserBackendDataHandler.getInstance().getBackendToken()
                    headers["Authorization"] = "Bearer $authToken"
                    return headers
                }
            }
            requestQueue.add(getRequest)
        } catch (e: Throwable){
            Log.e(BackService::class.java.name, "Back service error", e)
        }
    }

    fun doGetArray(
        method: String,
        onSuccess: Response.Listener<JSONObject>,
        onError: Response.ErrorListener = Response.ErrorListener { error -> Log.d("Error.Response", error.toString()) }
    ){
        try{
            val finalUrl = url+method
            val getRequest = object : JSONObjectFromArray(
                Method.GET,
                finalUrl, null,
                onSuccess,
                onError) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    val authToken = UserBackendDataHandler.getInstance().getBackendToken()
                    headers["Authorization"] = "Bearer $authToken"
                    return headers
                }
            }
            requestQueue.add(getRequest)
        } catch (e: Throwable){
            Log.e(BackService::class.java.name, "Back service error", e)
        }
    }

    fun doPost(
        method: String,
        listener: Response.Listener<JSONObject>,
        jsonRequest: JSONObject?,
        onError: Response.ErrorListener = Response.ErrorListener { error -> Log.d("Error.Response", error.toString()) }
    ) {
        try {
            val finalUrl = url + method
            val getRequest = object : JsonObjectRequest(
                Method.POST,
                finalUrl, jsonRequest,
                listener,
                onError
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    val authToken = UserBackendDataHandler.getInstance().getBackendToken()
                    headers["Authorization"] = "Bearer $authToken"
                    return headers
                }
            }

            requestQueue.add(getRequest)
        } catch (e: Throwable) {
            Log.e(BackService::class.java.name, "Back service error", e)
        }
    }

    fun doPatch(
        method: String,
        listener: Response.Listener<JSONObject>,
        jsonRequest: JSONObject?,
        onError: Response.ErrorListener = Response.ErrorListener { error ->
            Log.d(
                "Error.Response",
                error.toString()
            )
        }
    ) {
        try {
            val finalUrl = url + method
            val patchRequest = object : JsonObjectRequest(
                Method.PATCH,
                finalUrl, jsonRequest,
                listener,
                onError
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    val authToken = UserBackendDataHandler.getInstance().getBackendToken()
                    headers["Authorization"] = "Bearer $authToken"
                    return headers
                }
            }

            requestQueue.add(patchRequest)
        } catch (e: Throwable) {
            Log.e(BackService::class.java.name, "Back service error", e)
        }
    }
}

private open class JSONObjectFromArray(
    method: Int,
    url: String?,
    requestBody: String?,
    listener: Response.Listener<JSONObject>?,
    errorListener: Response.ErrorListener?
) : JsonRequest<JSONObject>(method, url, requestBody, listener, errorListener) {
    override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
        val parseCharset = HttpHeaderParser.parseCharset(response!!.headers, PROTOCOL_CHARSET)
        val jsonString = String(response.data, charset(parseCharset))
        val array = JSONArray(jsonString)
        return Response.success(JSONObject().put(SERVICE_ARRAY_RESPONSE, array),HttpHeaderParser.parseCacheHeaders(response))
    }

}

