package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.taller.tp.foodie.R

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
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            return true
        }

        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
    }
}
