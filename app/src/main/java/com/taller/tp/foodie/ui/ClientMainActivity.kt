package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R

class ClientMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_main)
    }

    fun profile(@Suppress("UNUSED_PARAMETER") view: View){
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    fun buildOrder(@Suppress("UNUSED_PARAMETER") view: View){
        startActivity(Intent(this, ClientOrderActivity::class.java))
    }
}
