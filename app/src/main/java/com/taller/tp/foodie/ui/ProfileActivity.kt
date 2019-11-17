package com.taller.tp.foodie.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.UserProfile
import com.taller.tp.foodie.model.requestHandlers.GetUserProfileRequestHandler
import com.taller.tp.foodie.services.ProfileService
import kotlinx.android.synthetic.main.activity_profile.*
import java.lang.ref.WeakReference

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        btn_change_profile.setOnClickListener {
            startActivity(Intent(applicationContext, ChangeProfileActivity::class.java))
        }

        getUserProfile()
    }

    private fun getUserProfile() {
        ProfileService(
            GetUserProfileRequestHandler(WeakReference(this))
        ).getUserProfile()
    }

    @SuppressLint("SetTextI18n")
    fun fillProfile(data: UserProfile) {
        full_name.text = data.name + " " + data.last_name
        profile_image.setImageURI(data.profile_image)
        reputation.rating = data.reputation

        email.text = data.email
        phone.text = data.phone

        deliveries.text = data.deliveries_completed.toString()
        sent_msg.text = data.messages_sent.toString()
    }

    override fun onRestart() {
        super.onRestart()

        getUserProfile()
    }
}

