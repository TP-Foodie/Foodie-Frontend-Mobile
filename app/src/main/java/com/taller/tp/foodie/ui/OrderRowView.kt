package com.taller.tp.foodie.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.Order
import kotlinx.android.synthetic.main.order_row.view.*

class OrderRowView (context: Context) : FrameLayout(context) {

    init {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutInflater.inflate(R.layout.order_row, this)
    }

    fun setOrderHeader(name: String) {
        order_name.text = name
    }

    fun setOrderDetail(status: String) {
        order_status.text = getStatusLabel(status)
    }

    private fun getStatusLabel(status: String): String {
        return when (status) {
            Order.STATUS.WAITING_STATUS.key -> "En espera"
            Order.STATUS.TAKEN_STATUS.key -> "En viaje"
            Order.STATUS.CANCELLED_STATUS.key -> "Cancelado"
            Order.STATUS.DELIVERED_STATUS.key -> "Entregado"
            else -> ""
        }
    }
}
