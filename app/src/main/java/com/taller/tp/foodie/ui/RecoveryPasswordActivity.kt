package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.common.auth.AuthErrors
import com.taller.tp.foodie.model.requestHandlers.UpdatePasswordRequestHandler
import com.taller.tp.foodie.services.AuthService
import com.taller.tp.foodie.utils.emailIsValid
import com.taller.tp.foodie.utils.passwordIsValid
import com.taller.tp.foodie.utils.passwordsAreEqualAndNotEmpty
import com.taller.tp.foodie.utils.tokenIsValid
import kotlinx.android.synthetic.main.activity_recovery_password.*
import java.lang.ref.WeakReference

class RecoveryPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery_password)
    }

    fun loginPage() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
    }

    fun recoveryPassword(view: View) {
        val validEmail = validateEmailAndUpdateUi()
        val validToken = validateTokenAndUpdateUi()
        val validPassword = validatePasswordAndUpdateUi()

        if (validEmail && validPassword && validToken){
            this.updatePassword()
        }
    }

    private fun updatePassword() {
        val requestHandler = UpdatePasswordRequestHandler(WeakReference(this))
        AuthService(requestHandler).updatePassowrd(
            email = email_field.text.toString(),
            password = password_field.text.toString(),
            token = token_field.text.toString()
        )
    }


    private fun validatePasswordAndUpdateUi(): Boolean {
        password_field_layout.error = null
        password_confirm_layout.error = null

        if (!passwordIsValid(password_field.text.toString())) {
            password_field_layout.error = AuthErrors.INVALID_PASSWORD_ERROR
            return false
        }

        if (!passwordsAreEqualAndNotEmpty(
                password_field.text.toString(),
                password_confirmation_field.text.toString()
            )
        ) {
            password_field_layout.error = AuthErrors.PASSWORD_CONFIRM_ERROR
            password_confirm_layout.error = AuthErrors.PASSWORD_CONFIRM_ERROR
            return false
        }

        return true
    }

    private fun validateEmailAndUpdateUi(): Boolean {
        email_field_layout.error = null

        if (!emailIsValid(email_field.text.toString())) {
            email_field_layout.error = AuthErrors.INVALID_EMAIL_ERROR
            return false
        }

        return true
    }

    private fun validateTokenAndUpdateUi(): Boolean {
        token_field_layout.error = null

        if (!tokenIsValid(email_field.text.toString())) {
            token_field_layout.error = AuthErrors.INVALID_EMAIL_ERROR
            return false
        }

        return true
    }

}
