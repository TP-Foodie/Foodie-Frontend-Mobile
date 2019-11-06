package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.common.auth.AuthErrors
import com.taller.tp.foodie.model.requestHandlers.SendTokenRequestHandler
import com.taller.tp.foodie.services.AuthService
import com.taller.tp.foodie.utils.emailIsValid
import kotlinx.android.synthetic.main.activity_token.*
import java.lang.ref.WeakReference

class TokenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token)
    }

    fun sendRecoveryToken(view: View){
        email_field_layout.error = null

        if (!emailIsValid(email_field.text.toString())) {
            email_field_layout.error = AuthErrors.INVALID_EMAIL_ERROR
        } else {
            this.sendToken()
        }
    }

    private fun sendToken() {
        val requestHandler = SendTokenRequestHandler(WeakReference(this))
        AuthService(requestHandler).sendToken(email_field.text.toString())
    }

    fun tokenPasswordPage(view: View) {
        val intent = Intent(applicationContext, RecoveryPasswordActivity::class.java)
        startActivity(intent)
    }

    fun goToRecoveryPasswordActivity() {
        val intent = Intent(applicationContext, RecoveryPasswordActivity::class.java)
        startActivity(intent)
    }
}
