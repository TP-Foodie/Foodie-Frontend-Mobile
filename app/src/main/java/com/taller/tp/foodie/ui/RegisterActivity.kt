package com.taller.tp.foodie.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.common.AuthErrors.EMAIL_ERROR
import com.taller.tp.foodie.model.common.AuthErrors.NAME_ERROR
import com.taller.tp.foodie.model.common.AuthErrors.PASSWORD_CONFIRM_ERROR
import com.taller.tp.foodie.model.common.AuthErrors.PASSWORD_ERROR
import com.taller.tp.foodie.model.common.AuthErrors.PHONE_ERROR
import com.taller.tp.foodie.model.requestHandlers.RegisterRequestHandler
import com.taller.tp.foodie.services.UserService
import com.taller.tp.foodie.utils.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    companion object {
        const val CLIENT_TYPE = "CUSTOMER"
        const val DELIVERY_TYPE = "DELIVERY"

        const val RESULT_OK = -1
        const val PICK_IMAGE_REQUEST = 111

        // for making passwords gone if federated
        const val LOGIN_TYPE = "login type"
        const val FEDERATED_LOGIN = 0
        const val NOT_FEDERATED_LOGIN = 1

        // extra data of user if federated
        const val FEDERATED_NAME = "name"
        const val FEDERATED_EMAIL = "email"
        const val FEDERATED_URL_PROFILE_IMAGE = "url profile image"
    }

    private lateinit var auth: FirebaseAuth
    private var filePath: Uri? = null
    private var profileImageIsFromFederated: Boolean = false
    private var loginType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        loginType = intent.getIntExtra(LOGIN_TYPE, NOT_FEDERATED_LOGIN)

        auth = FirebaseAuth.getInstance()

        setupClickListeners()

        checkLoginType()
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
            val validName = validateName()
            val validPhone = validatePhone()
            val validEmail = validateEmail()
            val validPassword = validatePassword()

            if (validName && validPhone && validEmail && validPassword) {
                registerUser()
            }
        }
    }

    private fun checkLoginType() {
        if (loginType == FEDERATED_LOGIN) {
            // hide passwords
            password_field_layout.visibility = View.GONE
            password_confirm_layout.visibility = View.GONE

            // set name, email and profile image
            name_field.setText(intent.getStringExtra(FEDERATED_NAME))
            email_field.setText(intent.getStringExtra(FEDERATED_EMAIL))
            profileImageIsFromFederated = true
            profile_image.setImageURI(intent.getStringExtra(FEDERATED_URL_PROFILE_IMAGE))
        } else {
            // show passwords
            password_field_layout.visibility = View.VISIBLE
            password_confirm_layout.visibility = View.VISIBLE
        }
    }

    private fun getUserType() : String {
        if (type_delivery.isChecked) {
            return DELIVERY_TYPE
        }

        return CLIENT_TYPE
    }

    private fun registerUser() {
        // register user in Firebase Auth
        auth.createUserWithEmailAndPassword(
            email_field.text.toString(),
            password_field.text.toString()
        ).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(this.localClassName, "createUserWithEmail:success")

                    // register user in backend
                    val requestHandler = RegisterRequestHandler(this)
                    UserService(this, requestHandler).register(
                        email_field.text.toString(),
                        password_field.text.toString(),
                        getUserType()
                    )
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(this.localClassName, "createUserWithEmail:failure", task.exception)
                }
            }
    }

    private fun validateName(): Boolean {
        name_field_layout.error = null

        if (!nameIsValid(name_field.text.toString())) {
            name_field_layout.error = NAME_ERROR
            return false
        }

        return true
    }

    private fun validatePhone(): Boolean {
        phone_field_layout.error = null

        if (!phoneIsValid(phone_field.text.toString())) {
            phone_field_layout.error = PHONE_ERROR
            return false
        }

        return true
    }


    private fun validatePassword(): Boolean {
        password_field_layout.error = null
        password_confirm_layout.error = null

        if (!passwordIsValid(password_field.text.toString())) {
            password_field_layout.error = PASSWORD_ERROR
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

    private fun validateEmail() : Boolean {
        email_field_layout.error = null

        if (!emailIsValid(email_field.text.toString())) {
            email_field_layout.error = EMAIL_ERROR
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
                profileImageIsFromFederated = false
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
