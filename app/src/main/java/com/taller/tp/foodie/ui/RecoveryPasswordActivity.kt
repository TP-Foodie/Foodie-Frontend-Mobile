package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R

class RecoveryPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery_password)
    }

    fun loginPage(view: View) {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
    }
}
