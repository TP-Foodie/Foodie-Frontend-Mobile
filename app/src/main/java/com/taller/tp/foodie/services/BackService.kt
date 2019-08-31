package com.taller.tp.foodie.services

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.*

open class BackService(ctx: Context){
    val url = "http://10.0.2.2:8090" // TODO HACER ALGO PARA Q LEA DE ALGUNA PROPERTY
    val context = ctx

    fun doGet(
        method: String,
        listener: Response.Listener<JSONObject>
    ){
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
    }

    fun doPost(
        method: String,
        listener: Response.Listener<JSONObject>,
        jsonRequest: JSONObject?
    ){
        val queue = Volley.newRequestQueue(context)
        val finalUrl = url+method
        val getRequest = JsonObjectRequest(
            Request.Method.POST,
            finalUrl, jsonRequest,
            listener,
            Response.ErrorListener { error ->
                Log.d("Error.Response", error.toString())
            }
        )
        queue.add(getRequest)
    }

}

