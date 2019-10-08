package com.taller.tp.foodie.services

import android.content.Context
import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

const val SERVICE_ARRAY_RESPONSE = "service-array-response"
open class BackService(ctx: Context){

    private val url = getUrl(ctx)
    private val authToken = UserBackendDataHandler(ctx).getBackendToken()

    private fun getUrl(ctx: Context): String {
        val stream = ctx.assets.open("environment.properties")
        val properties = Properties()
        properties.load(stream)
        return properties.getProperty("foodie-back.url")
    }

    val context = ctx

    fun doGetObject(
        method: String,
        onSuccess: Response.Listener<JSONObject>,
        onError: Response.ErrorListener = Response.ErrorListener { error -> Log.d("Error.Response", error.toString()) }
    ){
        try{
            val queue = Volley.newRequestQueue(context)
            val finalUrl = url+method
            val getRequest = object : JsonObjectRequest(
                Method.GET,
                finalUrl, null,
                onSuccess,
                onError) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $authToken"
                    return headers
                }
            }
            queue.add(getRequest)
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
            val queue = Volley.newRequestQueue(context)
            val finalUrl = url+method
            val getRequest = object : JSONObjectFromArray(
                Method.GET,
                finalUrl, null,
                onSuccess,
                onError) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $authToken"
                    return headers
                }
            }
            queue.add(getRequest)
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
            val queue = Volley.newRequestQueue(context)
            val finalUrl = url + method
            val getRequest = object : JsonObjectRequest(
                Method.POST,
                finalUrl, jsonRequest,
                listener,
                onError
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $authToken"
                    return headers
                }
            }

            queue.add(getRequest)
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
            val queue = Volley.newRequestQueue(context)
            val finalUrl = url + method

            val patchRequest = object : JsonObjectRequest(
                Method.PATCH,
                finalUrl, jsonRequest,
                listener,
                onError
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $authToken"
                    return headers
                }
            }

            queue.add(patchRequest)
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

