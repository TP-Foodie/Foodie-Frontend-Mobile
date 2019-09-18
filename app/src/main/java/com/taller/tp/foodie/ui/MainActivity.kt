package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R


@Suppress("UNUSED_PARAMETER")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun listPlaces(view: View){
        val placeChoice = Intent(this, PlaceChoiceActivity::class.java)
        startActivity(placeChoice)
    }

    fun login(view: View){
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun onRegister(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

}
