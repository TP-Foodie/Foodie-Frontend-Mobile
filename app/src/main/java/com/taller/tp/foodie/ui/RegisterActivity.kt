package com.taller.tp.foodie.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.taller.tp.foodie.R

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
            validateEmail()
            validatePassword()
        }
    }

    private fun validatePassword() {
        val passwordField = findViewById<TextView>(R.id.password_field)
        val passwordConfirmationField = findViewById<TextView>(R.id.password_confirmation_field)

        if (passwordField.text.isEmpty()) {
            findViewById<TextInputLayout>(R.id.password_field_layout).error = PASSWORD_ERROR
        }

        if (passwordConfirmationField.text != passwordField.text) {
            findViewById<TextInputLayout>(R.id.password_confirm_layout).error = PASSWORD_CONFIRM_ERROR
        }
    }

    private fun validateEmail() {
        val emailField = findViewById<TextView>(R.id.email_field)
        val emailLayout = findViewById<TextInputLayout>(R.id.email_field_layout)

        emailLayout.error = null

        if (emailField.text.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailField.text).matches()) {
            emailLayout.error = EMAIL_ERROR
        }
    }
}
