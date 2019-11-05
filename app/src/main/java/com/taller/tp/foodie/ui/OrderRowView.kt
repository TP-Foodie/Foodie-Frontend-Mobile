package com.taller.tp.foodie.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.taller.tp.foodie.R

class OrderRowView (context: Context) : FrameLayout(context) {

    init {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutInflater.inflate(R.layout.order_row, this)
    }

    fun setOrderHeader(number: Int){
        val header = String.format("Pedido Nº: %d", number)
        findViewById<TextView>(R.id.order_row_header).setText(header)
    }

    fun setOrderDetail(status: String, type: String){
        val detail = String.format("Estado %s: Tipo: %s", status, type)
        findViewById<TextView>(R.id.order_row_detail).setText(detail)
    }
}
