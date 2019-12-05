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
import kotlinx.android.synthetic.main.fragment_orders.*

const val DETAIL_ORDER_KEY = "DETAIL_ORDER_KEY"

class OrdersFragment : Fragment(),
    TabLayout.OnTabSelectedListener {


    private enum class TAB { PENDING, COMPLETED, FAVOUR }
    private val pendingStatus = intArrayOf(Order.STATUS.WAITING_STATUS.ordinal,
                                            Order.STATUS.TAKEN_STATUS.ordinal)
    private val completedStatus = intArrayOf(Order.STATUS.DELIVERED_STATUS.ordinal,
                                                Order.STATUS.CANCELLED_STATUS.ordinal)

    private val userType: String = UserBackendDataHandler.getInstance().getUserType()
    private var userId: String? = null

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
        OrderService(listOrdersRequestHandler).listByUser()
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
            0 -> populateList(TAB.PENDING)
            1 -> populateList(TAB.FAVOUR)
            2 -> populateList(TAB.COMPLETED)
        }

    }

    private fun populateList(tab: TAB){
        val filteredOrders: List<Order>
        if (userType == User.USER_TYPE.CUSTOMER.name){
            filteredOrders = filterByCustomer(tab, allOrders, userId!!)
        } else {
            filteredOrders = filterByDelivery(tab, allOrders)
        }

        if (activity != null) {
            val listAdapter = OrderListAdapter(activity!!, filteredOrders) { order: Order? ->
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
    }

    fun populateData(userId: String, orders: List<Order>) {
        this.userId = userId
        allOrders = orders
        populateList(TAB.PENDING)
    }

    private fun filterByCustomer(
        tab: TAB,
        orders: List<Order>,
        userId: String
    ): List<Order> {
        return orders.filter { o ->
            val userIsOwner = userId == o.getOwner()?.id
            val userCarryFavour =
                o.isFavour() && o.getDelivery() != null && userId == o.getDelivery()?.id
            when (tab) {
                TAB.PENDING -> userIsOwner && pendingStatus.contains(o.getStatus().ordinal)
                TAB.COMPLETED -> (userIsOwner || userCarryFavour) && completedStatus.contains(o.getStatus().ordinal)
                TAB.FAVOUR -> pendingStatus.contains(o.getStatus().ordinal)
            }
        }
    }

    private fun filterByDelivery(
        tab: TAB,
        orders: List<Order>
    ): List<Order> {
        return orders.filter { o ->
            when (tab) {
                TAB.PENDING -> pendingStatus.contains(o.getStatus().ordinal)
                TAB.COMPLETED -> completedStatus.contains(o.getStatus().ordinal)
                TAB.FAVOUR -> false
            }
        }
    }


    companion object {
        fun newInstance(): OrdersFragment = OrdersFragment()
    }

    override fun onResume() {
        super.onResume()
        loadOrdersData()
    }
}
