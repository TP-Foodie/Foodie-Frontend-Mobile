package com.taller.tp.foodie.model.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

class ImageStringConversor {

    fun imageToBase64String(image: Bitmap): String {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val byteArray = baos.toByteArray()

        val imageString = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return "data:image/png;base64,$imageString"
    }

    fun base64StringToBitmap(encodedImage: String): Bitmap {
        val decodedString = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}