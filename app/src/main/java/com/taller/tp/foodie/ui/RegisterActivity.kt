package com.taller.tp.foodie.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.View

import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.requestHandlers.RegisterRequestHandler
import com.taller.tp.foodie.services.UserService

const val EMAIL_ERROR = "Por favor ingrese un email válido"
const val PASSWORD_ERROR = "Por favor ingrese una contraseña válida"
const val PASSWORD_CONFIRM_ERROR = "Las contraseñas no coinciden"

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar!!.hide()

        val registerButton = findViewById<Button>(R.id.register_submit_btn)

        registerButton.setOnClickListener {
            val validEmail = validateEmail()
            val validPassword = validatePassword()

            if (validEmail && validPassword) {
                registerUser()
            }
        }
    }


    private fun registerUser() {
        val passwordField = findViewById<TextView>(R.id.password_field)
        val emailField = findViewById<TextView>(R.id.email_field)

        val requestHandler = RegisterRequestHandler(this)

        UserService(this, requestHandler).register(emailField.text.toString(), passwordField.text.toString())
    }

    private fun validatePassword() : Boolean {
        val passwordField = findViewById<TextView>(R.id.password_field)
        val passwordConfirmationField = findViewById<TextView>(R.id.password_confirmation_field)

        val passwordLayout = findViewById<TextInputLayout>(R.id.password_field_layout)
        val passwordConfirmLayout = findViewById<TextInputLayout>(R.id.password_confirm_layout)

        passwordLayout.error = null
        passwordConfirmLayout.error = null

        if (passwordField.text.isEmpty()) {
            passwordLayout.error = PASSWORD_ERROR
            return false
        }

        if (passwordConfirmationField.text.toString() != passwordField.text.toString()) {
            passwordConfirmLayout.error = PASSWORD_CONFIRM_ERROR
            return false
        }

        return true
    }

    private fun validateEmail() : Boolean {
        val emailField = findViewById<TextView>(R.id.email_field)
        val emailLayout = findViewById<TextInputLayout>(R.id.email_field_layout)

        emailLayout.error = null

        if (emailField.text.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailField.text).matches()) {
            emailLayout.error = EMAIL_ERROR
            return false
        }

        return true
    }
}
