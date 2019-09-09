package com.taller.tp.foodie.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.TextView
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.validators.AtSymbolValidator
import com.taller.tp.foodie.model.validators.EmptyValidator
import com.taller.tp.foodie.model.validators.LengthValidator
import com.taller.tp.foodie.model.validators.Validator


class RegisterActivity : AppCompatActivity() {

    private val emailValidators = arrayOf(
        EmptyValidator(),
        AtSymbolValidator()
    )

    private val passwordValidators = arrayOf(
        LengthValidator(),
        AtSymbolValidator()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar!!.hide()

        val emailField = findViewById<TextView>(R.id.email_field)
        val passwordField = findViewById<TextView>(R.id.password_field)
        val registerButton = findViewById<Button>(R.id.register_submit_btn)

        registerButton.setOnClickListener {
            validateEmail(emailField)
            validatePassword(passwordField)
        }
    }

    private fun validateWith(field: TextView, validators: Array<Validator>) {
        validators.forEach { validator -> validator.validate(field) }
    }

    private fun validatePassword(passwordField: TextView) {
        validateWith(passwordField, passwordValidators)
    }

    private fun validateEmail(emailField: TextView) {
        validateWith(emailField, emailValidators)
    }
}
