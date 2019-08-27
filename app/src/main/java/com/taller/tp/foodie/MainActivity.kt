package com.taller.tp.foodie

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun callGetMethod(view: View){
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2:8090/hola"

        val findViewById = findViewById<TextView>(R.id.get_response)

        // prepare the Request
        val getRequest = JsonObjectRequest(
            Request.Method.GET,
            url, null,
            Response.Listener { response ->
                Log.d("Response", response.toString())
                findViewById.text = String.format("%s %s", response.getString("hello"),response.getString("world"))
            },
            Response.ErrorListener { error ->
                Log.d("Error.Response", error.toString())
            }
        )
        queue.add(getRequest)
    }

    fun callPostMethod(view: View){
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2:8090/chau"

        val findViewById = findViewById<TextView>(R.id.post_response)

        val jsonRequest = JSONObject("{ \"hello\": \"HELLO\", \"world\": \"WORLD\"}")

        // prepare the Request
        val getRequest = JsonObjectRequest(
            Request.Method.POST,
            url, jsonRequest,
            Response.Listener { response ->
                Log.d("Response", response.toString())
                findViewById.text = response.toString()
            },
            Response.ErrorListener { error ->
                Log.d("Error.Response", error.toString())
            }
        )
        queue.add(getRequest)
    }
}
