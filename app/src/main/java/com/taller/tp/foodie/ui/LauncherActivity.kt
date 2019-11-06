package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import com.taller.tp.foodie.model.requestHandlers.CheckRegistrationRequestHandler
import com.taller.tp.foodie.services.AuthService
import java.lang.ref.WeakReference

class LauncherActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()

        val userIsLogged = isUserLoggedIn()

        if (!userIsLogged) {
            // go to login activity
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            // check if the user has type field in user object
            AuthService(CheckRegistrationRequestHandler(WeakReference(this)))
                .checkIfUserIsRegistered()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        // check token and user id are not empty
        val token = UserBackendDataHandler.getInstance().getBackendToken()

        if (token.isEmpty()) {
            return false
        }

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
    }
}
