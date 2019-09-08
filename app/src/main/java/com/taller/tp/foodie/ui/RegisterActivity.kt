package com.taller.tp.foodie.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.taller.tp.foodie.R

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar!!.hide()

        val email_field = findViewById<TextView>(R.id.email_field)
        val submit_button = findViewById<Button>(R.id.register_submit_btn)

        submit_button.setOnClickListener {
            email_field.error = "Por favor ingrese un email valido"
        }
    }
}
