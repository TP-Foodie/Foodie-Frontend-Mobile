package com.taller.tp.foodie.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.model.requestHandlers.ListOrdersRequestHandler
import com.taller.tp.foodie.services.OrderService

const val DETAIL_ORDER_KEY = "DETAIL_ORDER_KEY"

class OrdersActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        populateOrders(ArrayList())
        val listOrdersRequestHandler = ListOrdersRequestHandler(this)
        OrderService(this, listOrdersRequestHandler).list()
    }

    fun populateOrders(orders: ArrayList<Order>) {
        val listAdapter = OrderListAdapter(this, orders){ order: Order? ->
            val detailIntent = Intent(this, OrderDetailActivity::class.java).apply {
                putExtra(DETAIL_ORDER_KEY, order!!.id)
            }
            startActivity(detailIntent)
        }

        with(findViewById<ListView>(R.id.order_rv)) {
            adapter = listAdapter
            registerForContextMenu(this)
            emptyView = findViewById<View>(R.id.empty_order_view)
            this.setOnItemClickListener(listAdapter)
        }
    }

    @SuppressLint("ResourceType")
    class OrderListAdapter(context: Context,
                           demos: List<Order>,
                           val orderSelection: (order: Order?) -> Unit) :
        ArrayAdapter<Order>(context, R.id.order_row_header, demos),
        AdapterView.OnItemClickListener{

        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val order = (parent!!.getItemAtPosition(position)) as Order
            orderSelection(order)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val order: Order = getItem(position)!!
            return (convertView as? OrderRowView
                ?: OrderRowView(context)).apply {
                setOrderHeader(order.getNumber()!!)
                setOrderDetail(order.getStatus(), order.getType())
                contentDescription = order.id
            }
        }
    }
}
