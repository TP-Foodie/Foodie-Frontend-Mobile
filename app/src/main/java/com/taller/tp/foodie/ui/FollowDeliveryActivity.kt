package com.taller.tp.foodie.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.taller.tp.foodie.R


class FollowDeliveryActivity: FragmentActivity() {

    private var map: GoogleMap? = null

    private lateinit var deliveryId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow_delivery)

        deliveryId = intent.getStringExtra("delivery_id")

        if (map == null) {
            (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
                .getMapAsync {m: GoogleMap -> map = m}
        }
    }

}