package com.taller.tp.foodie.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.User
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import com.taller.tp.foodie.model.requestHandlers.ClientMainUserRequestHandler
import com.taller.tp.foodie.services.ProfileService
import com.taller.tp.foodie.services.TrackingService
import kotlinx.android.synthetic.main.activity_client_main.*
import java.lang.ref.WeakReference

class ClientMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProfileService(ClientMainUserRequestHandler(WeakReference(this))).getUserProfile()

        if (!isTrackingServiceRunning()) {
            Intent(this, TrackingService::class.java).also { intent ->
                applicationContext.startService(intent)
            }
        }
    }

    private fun isTrackingServiceRunning(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TrackingService::class.java.simpleName == service.service.className) {
                return true
            }
        }
        return false
    }


    private fun buildListeners() {
        bottom_navigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btn_nav_orders -> {
                    openFragment(OrdersFragment.newInstance())
                }
                R.id.btn_nav_order_something -> {
                    openFragment(ClientMainFragment.newInstance())
                }
                R.id.btn_nav_profile -> {
                    openFragment(ProfileFragment.newInstance())
                }
            }

            return@setOnNavigationItemSelectedListener true
        }
        bottom_navigation.selectedItemId = R.id.btn_nav_order_something
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    fun loadUserTypeComponents(user: User) {
        // save user type
        UserBackendDataHandler.getInstance().persistUserType(user.type.name)

        // setup main ui
        setContentView(R.layout.activity_client_main)
        buildListeners()
        showSuccessfullOrderMessage()
    }

    private fun showSuccessfullOrderMessage() {
        if (intent.getBooleanExtra(SUCCESSFUL_ORDER_KEY, false)) {
            val context = findViewById<View>(R.id.container)
            val snackbar = Snackbar.make(context, R.string.general_success, Snackbar.LENGTH_SHORT)
            snackbar.view.setBackgroundColor(Color.GREEN)
            snackbar.show()
        }
    }
}
