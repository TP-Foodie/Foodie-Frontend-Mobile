package com.taller.tp.foodie

import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    @Test
    fun should_show_welcome_message() {
        val mainActivity = buildActivity<MainActivity>(MainActivity::class.java).setup().get()
        val view = mainActivity.findViewById<TextView>(R.id.welcome_view)

        assertEquals("Bienvenido a Foodie!", view.text)
    }
}