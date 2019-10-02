package com.taller.tp.foodie.services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.*

open class BackService(ctx: Context){
    val url = getUrl(ctx)

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

            Log.d(BackService::class.java.name, "Final url: $finalUrl")

            queue.add(getRequest)
        } catch (e: Throwable) {
            Log.e(BackService::class.java.name, "Back service error", e)
        }
    }

}

