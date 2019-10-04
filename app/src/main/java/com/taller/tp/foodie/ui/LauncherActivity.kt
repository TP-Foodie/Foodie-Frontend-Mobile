package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.common.UserBackendDataHandler

class LauncherActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        if (isUserLoggedIn()) {
            // go to main activity
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            // go to login activity
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        finish()
    }

    private fun isUserLoggedIn(): Boolean {
        // check token and user id are not empty
        val backendDataHandler = UserBackendDataHandler(this)
        val token = backendDataHandler.getBackendToken()
        val userId = backendDataHandler.getUserId()

        if (token.isEmpty() || userId.isEmpty()) {
            return false
        }

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
    }
}
