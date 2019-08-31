package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun listPlaces(@Suppress("UNUSED_PARAMETER") view: View){
        val placeChoice = Intent(this, PlaceChoice::class.java)
        startActivity(placeChoice)
    }

//    fun callPostMethod(view: View){
//        val queue = Volley.newRequestQueue(this)
//        val url = "http://10.0.2.2:8090/chau"
//
//        val findViewById = findViewById<TextView>(R.id.post_response)
//
//        val jsonRequest = JSONObject("{ \"hello\": \"HELLO\", \"world\": \"WORLD\"}")
//
//        // prepare the Request
//        val getRequest = JsonObjectRequest(
//            Request.Method.POST,
//            url, jsonRequest,
//            Response.Listener { response ->
//                Log.d("Response", response.toString())
//                findViewById.text = response.toString()
//            },
//            Response.ErrorListener { error ->
//                Log.d("Error.Response", error.toString())
//            }
//        )
//        queue.add(getRequest)
//    }
}
