package com.taller.tp.foodie.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(val id: String, val name: String, val coordinate: Coordinate, val image: String) :
    Parcelable