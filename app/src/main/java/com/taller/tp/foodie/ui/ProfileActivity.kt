package com.taller.tp.foodie.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R

class ProfileActivity : AppCompatActivity() {

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initIntentData()
    }

    private fun initIntentData() {
        userId = intent.getStringExtra(CLIENT_ID_KEY)
    }


}
