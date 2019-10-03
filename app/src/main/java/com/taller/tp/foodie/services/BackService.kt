package com.taller.tp.foodie.services

import android.content.Context
import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

const val SERVICE_ARRAY_RESPONSE = "service-array-response"
open class BackService(ctx: Context){
    private val url = getUrl(ctx)

    private fun getUrl(ctx: Context): String {
        val stream = ctx.assets.open("environment.properties")
        val properties = Properties()
        properties.load(stream)
        return properties.getProperty("foodie-back.url")
    }

    val context = ctx

    fun doGetArray(
        method: String,
        onSuccess: Response.Listener<JSONObject>,
        jsonRequest: JSONObject?,
        onError: Response.ErrorListener = Response.ErrorListener { error -> Log.d("Error.Response", error.toString()) }
    ){
        try{
            val queue = Volley.newRequestQueue(context)
            val finalUrl = url+method
            val getRequest = JSONObjectFromArray(Request.Method.GET,
                finalUrl, jsonRequest.toString(),
                onSuccess,
                onError)
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
        return doGetArray(method, onSuccess, null, onError)
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
            val getRequest = JsonObjectRequest(
                Request.Method.POST,
                finalUrl, jsonRequest,
                listener,
                onError
            )
            queue.add(getRequest)
        } catch (e: Throwable) {
            Log.e(BackService::class.java.name, "Back service error", e)
        }
    }

}

private class JSONObjectFromArray(
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

