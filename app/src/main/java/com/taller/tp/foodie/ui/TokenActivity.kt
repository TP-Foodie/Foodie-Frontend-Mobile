package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R

class TokenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token)
    }

    fun sendRecoveryToken(view: View){
        val intent = Intent(applicationContext, RecoveryPasswordActivity::class.java)
        startActivity(intent)
    }

    fun tokenPasswordPage(view: View) {
        val intent = Intent(applicationContext, RecoveryPasswordActivity::class.java)
        startActivity(intent)
    }
}
