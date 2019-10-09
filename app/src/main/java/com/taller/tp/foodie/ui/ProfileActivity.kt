package com.taller.tp.foodie.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.UserProfile
import com.taller.tp.foodie.model.requestHandlers.GetUserProfileRequestHandler
import com.taller.tp.foodie.services.ProfileService
import kotlinx.android.synthetic.main.activity_profile.*
import java.lang.ref.WeakReference

class ProfileActivity : AppCompatActivity() {

    var userId: String? = null

    companion object {
        const val USER_ID_EXTRA = "userId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // TODO: anda si es que queremos mostrar la misma info sin importar si el userId
        // TODO: no es el del usuario actual.
        userId = intent.getStringExtra(USER_ID_EXTRA)

        getUserProfile()
    }

    private fun getUserProfile() {
        ProfileService(applicationContext, GetUserProfileRequestHandler(WeakReference(this)))
            .getUserProfile(userId)
    }

    @SuppressLint("SetTextI18n")
    fun fillProfile(data: UserProfile) {
        full_name.text = data.name + " " + data.last_name
        profile_image.setImageURI(data.profile_image, null)
        reputation.rating = data.reputation

        email.text = data.email
        phone.text = data.phone

        deliveries.text = data.deliveries_completed.toString()
        sent_msg.text = data.sent_messages.toString()
    }
}

