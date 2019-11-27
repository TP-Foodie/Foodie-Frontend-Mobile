package com.taller.tp.foodie.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.model.User
import com.taller.tp.foodie.model.common.UserBackendDataHandler
import com.taller.tp.foodie.model.requestHandlers.ListOrdersRequestHandler
import com.taller.tp.foodie.services.OrderService
import com.taller.tp.foodie.services.ProfileService
import kotlinx.android.synthetic.main.fragment_orders.*

const val DETAIL_ORDER_KEY = "DETAIL_ORDER_KEY"

class OrdersFragment : Fragment(),
    TabLayout.OnTabSelectedListener {


    private enum class TAB { PENDING, COMPLETED }
    private var selectedTab: TAB = TAB.PENDING
    private val pendingStatus = intArrayOf(Order.STATUS.WAITING_STATUS.ordinal,
                                            Order.STATUS.TAKEN_STATUS.ordinal)
    private val completedStatus = intArrayOf(Order.STATUS.DELIVERED_STATUS.ordinal,
                                                Order.STATUS.CANCELLED_STATUS.ordinal)

    private val userType: String = UserBackendDataHandler.getInstance().getUserType()

    private var allOrders: List<Order> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        order_list_tabs.addOnTabSelectedListener(this)
        loadOrdersData()
    }

    private fun loadOrdersData() {
        val listOrdersRequestHandler = ListOrdersRequestHandler(this)
        if (userType == User.USER_TYPE.DELIVERY.name)
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
        val listAdapter = OrderListAdapter(activity!!, orders) { order: Order? ->
            val detailIntent =
                Intent(activity?.applicationContext, OrderDetailActivity::class.java).apply {
                putExtra(DETAIL_ORDER_KEY, order!!.id)
                    putExtra(CLIENT_TYPE_KEY, userType)
            }
            startActivity(detailIntent)
        }

        with(order_rv) {
            adapter = listAdapter
            emptyView = findViewById<View>(R.id.empty_order_view)
            this.onItemClickListener = listAdapter
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
        when (tab!!.position) {
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


    companion object {
        fun newInstance(): OrdersFragment = OrdersFragment()
    }
}
