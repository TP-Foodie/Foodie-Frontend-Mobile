package com.taller.tp.foodie.ui

import android.graphics.Bitmap
import android.os.AsyncTask
import android.graphics.BitmapFactory
import android.widget.ImageView
import java.lang.ref.WeakReference
import java.net.URL


class BitmapLoader(imageView: ImageView, val url: URL) : AsyncTask<Int, Void, Bitmap>() {
    private var imageViewReference: WeakReference<ImageView>? = null

    init{
        imageViewReference = WeakReference(imageView)
    }

    override fun doInBackground(vararg params: Int?): Bitmap {
        return BitmapFactory.decodeStream(url.openConnection().getInputStream())
    }

    override fun onPostExecute(result: Bitmap?) {
        var bitmap = result
        if (isCancelled) bitmap = null
        if (imageViewReference != null && bitmap != null){
            imageViewReference!!.get()!!.setImageBitmap(bitmap)
        }
    }
}