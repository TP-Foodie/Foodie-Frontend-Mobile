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
        val placeChoice = Intent(this, PlaceChoiceActivity::class.java)
        startActivity(placeChoice)
    }

    fun onRegister(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

}
