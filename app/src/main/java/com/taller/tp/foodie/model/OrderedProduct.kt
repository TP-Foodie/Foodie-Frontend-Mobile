package com.taller.tp.foodie.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderedProduct(
    var quantity: Int, var productFetched: ProductFetched
) : Parcelable

@Parcelize
data class ListOrderderProduct(
    var orderedProductsList: MutableList<OrderedProduct>
) : Parcelable