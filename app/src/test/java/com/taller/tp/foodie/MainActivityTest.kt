package com.taller.tp.foodie

import android.widget.TextView
import com.taller.tp.foodie.ui.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import java.util.*


@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    @Test
    fun should_show_welcome_message() {
        val mainActivity = buildActivity<MainActivity>(MainActivity::class.java).setup().get()
        val view = mainActivity.findViewById<TextView>(R.id.welcome_view)

        assertEquals("Bienvenido a Foodie!", view.text)
    }

    @Ignore("mock service")
    fun should_show_list_places() {
        val mainActivity = buildActivity<MainActivity>(MainActivity::class.java).setup().get()
        val stream = mainActivity.baseContext.assets.open("environment.properties")
        val properties = Properties()
        properties.load(stream)
    }
}