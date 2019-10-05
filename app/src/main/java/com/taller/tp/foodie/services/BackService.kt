package com.taller.tp.foodie.services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import org.json.JSONObject
import java.util.*

open class BackService(ctx: Context){
    private val url = getUrl(ctx)

    private fun getUrl(ctx: Context): String {
        val stream = ctx.assets.open("environment.properties")
        val properties = Properties()
        properties.load(stream)
        return properties.getProperty("debug-foodie-back.emulator-url")
    }

    val context = ctx

    fun doGet(
        method: String,
        listener: Response.Listener<JSONObject>
    ){
        try{
            val queue = Volley.newRequestQueue(context)
            val finalUrl = url+method
            val getRequest = JsonObjectRequest(
                Request.Method.GET,
                finalUrl, null,
                listener,
                Response.ErrorListener { error ->
                    Log.d("Error.Response", error.toString())
                }
            )
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

            val authToken = UserBackendDataHandler(context).getBackendToken()

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

