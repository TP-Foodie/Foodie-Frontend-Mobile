package com.taller.tp.foodie.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.validators.AtSymbolValidator
import com.taller.tp.foodie.model.validators.EmptyValidator

val EMAIL_ERROR = "Por favor ingrese un email válido"

class RegisterActivity : AppCompatActivity() {

    private val emailValidators = arrayOf(
        EmptyValidator(EMAIL_ERROR),
        AtSymbolValidator(EMAIL_ERROR)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar!!.hide()

        val emailField = findViewById<TextView>(R.id.email_field)
        val registerButton = findViewById<Button>(R.id.register_submit_btn)

        registerButton.setOnClickListener {
            validateEmail(emailField)
        }
    }

    private fun validateEmail(emailField: TextView) {
        emailValidators.forEach { validator -> validator.validate(emailField) }
    }
}
