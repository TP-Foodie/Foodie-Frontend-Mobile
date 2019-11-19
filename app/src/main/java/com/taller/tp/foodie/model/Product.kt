package com.taller.tp.foodie.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductFetched(
    var id: String, var name: String, var description: String, var price: Int,
    var place: Place, var image: String
) : Parcelable