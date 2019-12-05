package com.taller.tp.foodie.ui

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.common.ImageStringConversor
import com.taller.tp.foodie.model.common.auth.AuthErrors
import com.taller.tp.foodie.model.requestHandlers.ChangeProfileRequestHandler
import com.taller.tp.foodie.services.UserService
import com.taller.tp.foodie.utils.lastNameIsValid
import com.taller.tp.foodie.utils.nameIsValid
import com.taller.tp.foodie.utils.phoneIsValid
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_change_profile.*
import java.lang.ref.WeakReference

class ChangeProfileActivity : AppCompatActivity() {

    companion object {
        const val RESULT_OK = -1
        const val PICK_IMAGE_REQUEST = 111

        // update fields
        const val NAME_FIELD = "name"
        const val LAST_NAME_FIELD = "last_name"
        //const val EMAIL_FIELD = "email"
        const val PHONE_FIELD = "phone"
        const val PROFILE_IMAGE_FIELD = "profile_image"
    }

    private var profileImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_profile)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        edit_profile_image.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(
                Intent.createChooser(intent, "Elegir Imagen"),
                RegisterActivity.PICK_IMAGE_REQUEST
            )
        }

        btn_confirm_changes.setOnClickListener {
            val validName = validateName()
            val validLastName = validateLastName()
            val validPhone = validatePhone()
            //val validEmail = validateEmail()

            val data = mutableMapOf<String, String>()
            if (validName) {
                data[NAME_FIELD] = name_field.text?.trim().toString()
            } else if (name_field.text?.trim().toString().isNotEmpty()) {
                name_field_layout.error = AuthErrors.NAME_ERROR
                return@setOnClickListener
            }

            if (validLastName) {
                data[LAST_NAME_FIELD] = last_name_field.text?.trim().toString()
            } else if (last_name_field.text?.trim().toString().isNotEmpty()) {
                last_name_field_layout.error = AuthErrors.LAST_NAME_ERROR
                return@setOnClickListener
            }

            if (validPhone) {
                data[PHONE_FIELD] = phone_field.text?.trim().toString()
            } else if (phone_field.text?.trim().toString().isNotEmpty()) {
                phone_field_layout.error = AuthErrors.PHONE_ERROR
                return@setOnClickListener
            }

            /*
            if (validEmail) {
                data[EMAIL_FIELD] = email_field.text?.trim().toString()
            } else if (email_field.text?.trim().toString().isNotEmpty()) {
                email_field_layout.error = AuthErrors.INVALID_EMAIL_ERROR
                return@setOnClickListener
            }
             */

            val image = profileImage
            if (image != null) {
                data[PROFILE_IMAGE_FIELD] = ImageStringConversor().imageToBase64String(image)
            }

            if (data.isNotEmpty()) {
                changeUserProfile(data)
            } else {
                onBackPressed()
            }
        }
    }

    private fun changeUserProfile(data: Map<String, String>) {
        UserService(ChangeProfileRequestHandler(WeakReference(this))).changeUserProfile(data)
    }

    private fun validateName(): Boolean {
        name_field_layout.error = null

        if (!nameIsValid(name_field.text.toString())) {
            return false
        }

        return true
    }

    private fun validateLastName(): Boolean {
        last_name_field_layout.error = null

        if (!lastNameIsValid(last_name_field.text.toString())) {
            return false
        }

        return true
    }

    private fun validatePhone(): Boolean {
        phone_field_layout.error = null

        if (!phoneIsValid(phone_field.text.toString())) {
            return false
        }

        return true
    }

    /*
    private fun validateEmail(): Boolean {
        email_field_layout.error = null

        if (!emailIsValid(email_field.text.toString())) {
            return false
        }

        return true
    }
     */

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

                ErrorHandler.handleError(change_profile_layout)
            }
        }
    }
}
