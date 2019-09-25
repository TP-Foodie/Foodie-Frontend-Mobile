package com.taller.tp.foodie.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.requestHandlers.RegisterRequestHandler
import com.taller.tp.foodie.services.UserService
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    companion object {
        const val EMAIL_ERROR = "Por favor ingrese un email válido"
        const val PASSWORD_ERROR = "Por favor ingrese una contraseña válida"
        const val PASSWORD_CONFIRM_ERROR = "Las contraseñas no coinciden"
        const val CLIENT_TYPE = "CUSTOMER"
        const val DELIVERY_TYPE = "DELIVERY"

        const val RESULT_OK = -1
        const val PICK_IMAGE_REQUEST = 111
    }

    private var filePath: Uri? = null
    private var profileImageIsFromProvider: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        edit_profile_image.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(
                Intent.createChooser(intent, "Elegir Imagen"),
                PICK_IMAGE_REQUEST
            )
        }

        btn_register.setOnClickListener {
            val validEmail = validateEmail()
            val validPassword = validatePassword()

            if (validEmail && validPassword) {
                registerUser()
            }
        }
    }

    private fun getUserType() : String {
        if (type_delivery.isChecked) {
            return DELIVERY_TYPE
        }

        return CLIENT_TYPE
    }

    private fun registerUser() {
        val passwordField = findViewById<TextView>(R.id.password_field)
        val emailField = findViewById<TextView>(R.id.email_field)

        val requestHandler = RegisterRequestHandler(this)

        UserService(this, requestHandler).register(
            emailField.text.toString(),
            passwordField.text.toString(),
            getUserType()
        )
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val image = data.data

            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(image)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(1, 1)
                .start(this)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                // get Uri from result
                filePath = result.uri

                //Setting image to ImageView
                profile_image.setImageURI(filePath.toString())

                //Update profile image as NOT from provider
                profileImageIsFromProvider = false
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
