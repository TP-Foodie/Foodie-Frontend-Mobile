package com.taller.tp.foodie.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.TextView
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.validators.AtSymbolValidator
import com.taller.tp.foodie.model.validators.EmptyValidator
import com.taller.tp.foodie.model.validators.MatchValidator

const val EMAIL_ERROR = "Por favor ingrese un email válido"
const val PASSWORD_ERROR = "Por favor ingrese una contraseña válida"
const val PASSWORD_CONFIRM_ERROR = "Las contraseñas no coinciden"

class RegisterActivity : AppCompatActivity() {

    private val emailValidators = arrayOf(
        EmptyValidator(EMAIL_ERROR),
        AtSymbolValidator(EMAIL_ERROR)
    )

    private val passwordValidator = EmptyValidator(PASSWORD_ERROR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar!!.hide()

        val emailField = findViewById<TextView>(R.id.email_field)
        val passwordField = findViewById<TextView>(R.id.password_field)
        val passwordConfirmationField = findViewById<TextView>(R.id.password_confirmation_field)

        val registerButton = findViewById<Button>(R.id.register_submit_btn)

        registerButton.setOnClickListener {
            validateEmail(emailField)
            validatePassword(passwordField, passwordConfirmationField)
        }
    }

    private fun validatePassword(passwordField: TextView, passwordConfirmationField: TextView) {
        passwordValidator.validate(passwordField)
        MatchValidator(passwordField.text.toString(), PASSWORD_CONFIRM_ERROR)
            .validate(passwordConfirmationField)
    }

    private fun validateEmail(emailField: TextView) {
        emailValidators.forEach { validator -> validator.validate(emailField) }
    }
}
