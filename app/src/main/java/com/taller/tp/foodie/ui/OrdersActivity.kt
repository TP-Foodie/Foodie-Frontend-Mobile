package com.taller.tp.foodie.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.model.requestHandlers.ListOrdersRequestHandler
import com.taller.tp.foodie.services.OrderService

const val DETAIL_ORDER_KEY = "DETAIL_ORDER_KEY"

class OrdersActivity : AppCompatActivity(),
    TabLayout.OnTabSelectedListener {


    private enum class TAB { PENDING, COMPLETED }
    private var selectedTab: TAB = TAB.PENDING
    private val pendingStatus = intArrayOf(Order.STATUS.WAITING_STATUS.ordinal,
                                            Order.STATUS.TAKEN_STATUS.ordinal)
    private val completedStatus = intArrayOf(Order.STATUS.DELIVERED_STATUS.ordinal)

    private var allOrders: ArrayList<Order> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        val tabs = findViewById<TabLayout>(R.id.order_list_tabs)
        tabs.addOnTabSelectedListener(this)
        val listOrdersRequestHandler = ListOrdersRequestHandler(this)
        OrderService(this, listOrdersRequestHandler).list()
    }

    fun populateOrders() {
        val orders = allOrders.filter { o ->
            when(selectedTab){
                TAB.PENDING -> pendingStatus.contains(o.getStatus().ordinal)
                TAB.COMPLETED -> completedStatus.contains(o.getStatus().ordinal)
            }
        }
        val listAdapter = OrderListAdapter(this, orders){ order: Order? ->
            val detailIntent = Intent(this, OrderDetailActivity::class.java).apply {
                putExtra(DETAIL_ORDER_KEY, order!!.id)
            }
            startActivity(detailIntent)
        }

        with(findViewById<ListView>(R.id.order_rv)) {
            adapter = listAdapter
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
                setOrderDetail(order.getStatus().key, order.getType())
                contentDescription = order.id
            }
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {}

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val pos = tab!!.position
        when (pos){
            0 -> selectedTab = TAB.PENDING
            1 -> selectedTab = TAB.COMPLETED
        }
        populateOrders()
    }

    fun setOrders(orders: java.util.ArrayList<Order>) {
        allOrders = orders
    }
}
