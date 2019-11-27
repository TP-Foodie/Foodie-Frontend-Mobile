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
import com.taller.tp.foodie.model.User
import com.taller.tp.foodie.model.requestHandlers.ListOrdersRequestHandler
import com.taller.tp.foodie.services.OrderService
import com.taller.tp.foodie.services.ProfileService

const val DETAIL_ORDER_KEY = "DETAIL_ORDER_KEY"

class OrdersActivity : AppCompatActivity(),
    TabLayout.OnTabSelectedListener {


    private enum class TAB { PENDING, COMPLETED }
    private var selectedTab: TAB = TAB.PENDING
    private val pendingStatus = intArrayOf(Order.STATUS.WAITING_STATUS.ordinal,
                                            Order.STATUS.TAKEN_STATUS.ordinal)
    private val completedStatus = intArrayOf(Order.STATUS.DELIVERED_STATUS.ordinal,
                                                Order.STATUS.CANCELLED_STATUS.ordinal)

    lateinit var userType: User.USER_TYPE

    private var allOrders: List<Order> = ArrayList()

    override fun onResume() {
        super.onResume()
        loadOrdersData()
    }

    private fun loadUserType() {
        val intentUserType = intent.getStringExtra(CLIENT_TYPE_KEY)
        this.userType = User.USER_TYPE.valueOf(intentUserType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        loadOrdersData()
    }

    private fun loadOrdersData() {
        loadUserType()

        val tabs = findViewById<TabLayout>(R.id.order_list_tabs)
        tabs.addOnTabSelectedListener(this)
        val listOrdersRequestHandler = ListOrdersRequestHandler(this)
        if (userType == User.USER_TYPE.DELIVERY)
            listOrdersRequestHandler.byDelivery()
        OrderService(listOrdersRequestHandler).listByUser(userType)
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
                putExtra(CLIENT_TYPE_KEY, userType.name)
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
        ArrayAdapter<Order>(context, R.id.order_name, demos),
        AdapterView.OnItemClickListener{

        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val order = (parent!!.getItemAtPosition(position)) as Order
            orderSelection(order)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val order: Order = getItem(position)!!
            return (convertView as? OrderRowView
                ?: OrderRowView(context)).apply {
                setOrderHeader(order.getName()!!)
                setOrderDetail(order.getStatus().key)
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

    fun setOrders(orders: List<Order>) {
        allOrders = orders
    }

    fun filterOrders(deliveryId: String) {
        val filtered = allOrders.filter { order ->
            order.getDelivery() != null && order.getDelivery()!!.id == deliveryId
        }
        setOrders(filtered)
    }
    fun askForDelivery() {
        ProfileService(ListOrdersRequestHandler(this).forFilter()).getUserProfile()
    }


}
