package com.taller.tp.foodie.ui

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.common.auth.AuthErrors.INVALID_EMAIL_ERROR
import com.taller.tp.foodie.model.common.auth.AuthErrors.INVALID_PASSWORD_ERROR
import com.taller.tp.foodie.model.common.auth.AuthErrors.LAST_NAME_ERROR
import com.taller.tp.foodie.model.common.auth.AuthErrors.NAME_ERROR
import com.taller.tp.foodie.model.common.auth.AuthErrors.PASSWORD_CONFIRM_ERROR
import com.taller.tp.foodie.model.common.auth.AuthErrors.PHONE_ERROR
import com.taller.tp.foodie.model.requestHandlers.EmailAuthFromRegisterRequestHandler
import com.taller.tp.foodie.model.requestHandlers.RegisterRequestHandler
import com.taller.tp.foodie.services.AuthService
import com.taller.tp.foodie.services.UserService
import com.taller.tp.foodie.utils.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_register.*
import java.lang.ref.WeakReference

class RegisterActivity : AppCompatActivity() {

    companion object {
        const val RESULT_OK = -1
        const val PICK_IMAGE_REQUEST = 111
    }

    private var profileImage: Bitmap? = null

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
            val validName = validateNameAndUpdateUi()
            val validLastName = validateLastNameAndUpdateUi()
            val validPhone = validatePhoneAndUpdateUi()
            val validEmail = validateEmailAndUpdateUi()
            val validPassword = validatePasswordAndUpdateUi()

            if (validName && validLastName && validPhone && validEmail && validPassword) {
                // send email and password to backend
                registerUserInBackend()
            }
        }
    }

    private fun registerUserInBackend() {
        // register user in backend
        val requestHandler = RegisterRequestHandler(WeakReference(this))
        UserService(this.applicationContext, requestHandler).register(
            email_field.text.toString(),
            password_field.text.toString(),
            name_field.text.toString(),
            last_name_field.text.toString(),
            phone_field.text.toString(),
            profileImage
        )
    }

    /*
    *   Called from AuthRequestHandler onSuccess
    *
     */
    fun authenticateWithBackend() {
        // authenticate user with backend
        AuthService(
            this.applicationContext,
            EmailAuthFromRegisterRequestHandler(WeakReference(this))
        )
            .emailAndPasswordAuthenticationWithBackend(
                email_field.text.toString(),
                password_field.text.toString()
            )
    }

    private fun validateNameAndUpdateUi(): Boolean {
        name_field_layout.error = null

        if (!nameIsValid(name_field.text.toString())) {
            name_field_layout.error = NAME_ERROR
            return false
        }

        return true
    }

    private fun validateLastNameAndUpdateUi(): Boolean {
        last_name_field_layout.error = null

        if (!lastNameIsValid(last_name_field.text.toString())) {
            last_name_field_layout.error = LAST_NAME_ERROR
            return false
        }

        return true
    }

    private fun validatePhoneAndUpdateUi(): Boolean {
        phone_field_layout.error = null

        if (!phoneIsValid(phone_field.text.toString())) {
            phone_field_layout.error = PHONE_ERROR
            return false
        }

        return true
    }


    private fun validatePasswordAndUpdateUi(): Boolean {
        password_field_layout.error = null
        password_confirm_layout.error = null

        if (!passwordIsValid(password_field.text.toString())) {
            password_field_layout.error = INVALID_PASSWORD_ERROR
            return false
        }

        if (!passwordsAreEqualAndNotEmpty(
                password_field.text.toString(),
                password_confirmation_field.text.toString()
            )
        ) {
            password_field_layout.error = PASSWORD_CONFIRM_ERROR
            password_confirm_layout.error = PASSWORD_CONFIRM_ERROR
            return false
        }

        return true
    }

    private fun validateEmailAndUpdateUi(): Boolean {
        email_field_layout.error = null

        if (!emailIsValid(email_field.text.toString())) {
            email_field_layout.error = INVALID_EMAIL_ERROR
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
                // Setting image to ImageView
                profile_image.setImageURI(result.uri.toString())

                profileImage = MediaStore.Images.Media.getBitmap(this.contentResolver, result.uri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // update UI
                Log.e(this.localClassName, "Error al hacer crop: ${result.error.message}")

                ErrorHandler.handleError(register_layout)
            }
        }
    }
}
