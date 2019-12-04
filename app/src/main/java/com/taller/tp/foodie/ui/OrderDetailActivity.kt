package com.taller.tp.foodie.ui

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.Chat
import com.taller.tp.foodie.model.ChatFetched
import com.taller.tp.foodie.model.Order
import com.taller.tp.foodie.model.User
import com.taller.tp.foodie.model.common.HeavyDataTransferingHandler
import com.taller.tp.foodie.model.requestHandlers.AssignOrderChatRequestHandler
import com.taller.tp.foodie.model.requestHandlers.CreateChatRequestHandler
import com.taller.tp.foodie.model.requestHandlers.OrderDetailRequestHandler
import com.taller.tp.foodie.model.requestHandlers.RateUserRequestHandler
import com.taller.tp.foodie.services.ChatService
import com.taller.tp.foodie.services.OrderService
import com.taller.tp.foodie.services.ProfileService
import com.taller.tp.foodie.services.UserRatingService
import com.taller.tp.foodie.ui.ui_adapters.OrderDetailProductsAdapter
import kotlinx.android.synthetic.main.activity_order_detail.*
import org.json.JSONObject
import java.lang.ref.WeakReference

class OrderDetailActivity : AppCompatActivity(), RateUserListener {

    private var order: Order? = null
    private lateinit var orderAsJson: JSONObject
    private lateinit var userType: User.USER_TYPE
    private var userId: String? = null
    private var isFavour: Boolean = false

    private var updateIsUnassign = false

    private fun loadUserType() {
        val handler = OrderDetailRequestHandler(this).getOwner()
        ProfileService(handler).getUserProfile()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        loadUserType()

        val actionsButton = findViewById<Button>(R.id.order_actions_button)
        actionsButton.setOnClickListener { openContextMenu(it) }
    }

    fun populateOrder(){
        val orderId = intent.getStringExtra(DETAIL_ORDER_KEY)
        if (orderId != null) {
            OrderService(OrderDetailRequestHandler(this)).find(orderId)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val deliverOption = menu!!.getItem(0).setVisible(false)
        val unassignOption = menu.getItem(1).setVisible(false)
        val assignOption = menu.getItem(2).setVisible(false)
        val cancelOption = menu.getItem(3).setVisible(false)
        val chatOption = menu.getItem(4).setVisible(false)
        val followDelivery = menu.getItem(5).setVisible(false)
        val rateDelivery = menu.getItem(6).setVisible(false)
        val rateOwner = menu.getItem(7).setVisible(false)

        if (isFavour){
            optionsFavourMenu(cancelOption,
                assignOption,
                chatOption,
                followDelivery,
                deliverOption,
                unassignOption,
                rateDelivery,
                rateOwner
            )
        } else {
            optionsOrderMenu(
                cancelOption,
                assignOption,
                chatOption,
                followDelivery,
                deliverOption,
                unassignOption,
                rateDelivery,
                rateOwner
            )
        }
        return true
    }

    private fun optionsOrderMenu(
        cancelOption: MenuItem,
        assignOption: MenuItem,
        chatOption: MenuItem,
        followDelivery: MenuItem,
        deliverOption: MenuItem,
        unassignOption: MenuItem,
        rateDelivery: MenuItem,
        rateOwner: MenuItem
    ) {
        when (order!!.getStatus()) {
            Order.STATUS.WAITING_STATUS -> {
                if (userType == User.USER_TYPE.CUSTOMER) {
                    cancelOption.isVisible = true
                    assignOption.isVisible = true
                }
            }
            Order.STATUS.TAKEN_STATUS -> {
                chatOption.isVisible = true
                if (userType == User.USER_TYPE.CUSTOMER) {
                    cancelOption.isVisible = true
                    followDelivery.isVisible = true
                } else {
                    deliverOption.isVisible = true
                    unassignOption.isVisible = true
                }
            }
            Order.STATUS.CANCELLED_STATUS -> {
                chatOption.isVisible = true
            }
            Order.STATUS.DELIVERED_STATUS -> {
                chatOption.isVisible = true
                if (userType == User.USER_TYPE.CUSTOMER && !order?.isDeliveryRated()!!) {
                    rateDelivery.isVisible = true
                } else if (userType == User.USER_TYPE.DELIVERY && !order?.isOwnerRated()!!) {
                    rateOwner.isVisible = true
                }
            }
        }
    }

    private fun optionsFavourMenu(
        cancelOption: MenuItem,
        assignOption: MenuItem,
        chatOption: MenuItem,
        followDelivery: MenuItem,
        deliverOption: MenuItem,
        unassignOption: MenuItem,
        rateDelivery: MenuItem,
        rateOwner: MenuItem
    ) {
        val isOwner = order!!.getOwner()!!.id.equals(userId)
        when (order!!.getStatus()) {
            Order.STATUS.WAITING_STATUS -> {
                if (isOwner)
                    cancelOption.isVisible = true
                else
                    assignOption.isVisible = true
            }
            Order.STATUS.TAKEN_STATUS -> {
                chatOption.isVisible = true
                if (isOwner) {
                    cancelOption.isVisible = true
                    followDelivery.isVisible = true
                } else {
                    deliverOption.isVisible = true
                    unassignOption.isVisible = true
                }
            }
            Order.STATUS.CANCELLED_STATUS -> {
                chatOption.isVisible = true
            }
            Order.STATUS.DELIVERED_STATUS -> {
                chatOption.isVisible = true
                if (isOwner && !order?.isDeliveryRated()!!) {
                    rateDelivery.isVisible = true
                } else if (!isOwner && !order?.isOwnerRated()!!) {
                    rateOwner.isVisible = true
                }
            }
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.order_action_menu, menu)
        onPrepareOptionsMenu(menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId){
            R.id.deliver_order_option -> {
                OrderService(OrderDetailRequestHandler(this).forUpdate())
                    .deliverOrder(order!!)
                return true
            }
            R.id.unassign_order_option -> {
                OrderService(OrderDetailRequestHandler(this).forUpdate())
                    .unassignOrder(order!!)
                updateIsUnassign = true
                return true
            }
            R.id.assign_order_option -> {
                if (isFavour){
                    val handler = OrderDetailRequestHandler(this).forUpdateFavour()
                    OrderService(handler).confirmOrder(order!!, userId!!)
                    return true
                }else {
                    val intent = Intent(this, ConfirmOrderActivity::class.java)
                    HeavyDataTransferingHandler.getInstance().saveOrderJson(orderAsJson.toString())
                    //intent.putExtra(CLIENT_NEW_ORDER_KEY, orderAsJson.toString())
                    startActivity(intent)
                    return true
                }
            }
            R.id.cancel_order_option -> {
                OrderService(OrderDetailRequestHandler(this).forUpdate())
                    .cancelOrder(order!!)
                return true
            }
            R.id.chat_order_option -> {
                val intent = Intent(applicationContext, ChatActivity::class.java)
                intent.putExtra(ChatActivity.CHAT_ID, order?.getIdChat())
                intent.putExtra(ChatActivity.ORDER_STATUS, order?.getStatus()?.key)
                startActivity(intent)
                return true
            }
            R.id.follow_delivery -> {
                val intent = Intent(applicationContext, FollowDeliveryActivity::class.java)
                intent.putExtra("delivery_id", order!!.getDelivery()!!.id)
                startActivity(intent)
                return true
            }
            R.id.rate_delivery_option -> {
                rateUserMenuOptionSelected(order?.getDelivery()!!)
                return true
            }
            R.id.rate_owner_option -> {
                rateUserMenuOptionSelected(order?.getOwner()!!)
                return true
            }
            else -> return false
        }
    }

    private fun rateUserMenuOptionSelected(user: User) {
        val fm = supportFragmentManager
        val rateUserDialog = RateUserDialogFragment.newInstance(user, this)
        rateUserDialog?.show(fm, "fragment_alert")
    }

    override fun onFinishRateUser(userRated: User, rating: Int) {
        UserRatingService(RateUserRequestHandler(WeakReference(this))).rateUser(
            userRated, rating, order!!
        )
    }

    private fun setupProductsLayout() {
        val manager = LinearLayoutManager(applicationContext)
        manager.reverseLayout = true
        products_list.layoutManager = manager
    }

    fun populateFields(order: Order, response: JSONObject) {
        this.orderAsJson = response
        this.order = order

        if (userType == User.USER_TYPE.DELIVERY) {
            rl_owner.visibility = View.VISIBLE
            owner_name.text = order.getOwner()!!.name
            owner_image.setImageURI(order.getOwner()?.image)

            val priceLabel = "Envío: $%.2f"
            quotation_price.visibility = View.VISIBLE
            quotation_price.text = String.format(priceLabel, order.getQuotation())

            var totalPrice = 0.0
            totalPrice += order.getQuotation()!!
            for (prod in order.getProducts()) {
                totalPrice += (prod.quantity * prod.productFetched.price)
            }
            val totalPriceLabel = "Total: $%.2f"
            total_price.visibility = View.VISIBLE
            total_price.text = String.format(totalPriceLabel, totalPrice)
        } else if (userType == User.USER_TYPE.CUSTOMER && order.getDelivery() != null) {
            rl_delivery.visibility = View.VISIBLE
            delivery_name.text = order.getDelivery()!!.name
            delivery_image.setImageURI(order.getDelivery()?.image)

            val priceLabel = "Envío: $%.2f"
            quotation_price.visibility = View.VISIBLE
            quotation_price.text = String.format(priceLabel, order.getQuotation())

            var totalPrice = 0.0
            totalPrice += order.getQuotation()!!
            for (prod in order.getProducts()) {
                totalPrice += (prod.quantity * prod.productFetched.price)
            }
            val totalPriceLabel = "Total: $%.2f"
            total_price.visibility = View.VISIBLE
            total_price.text = String.format(totalPriceLabel, totalPrice)
        }

        order_name.text = order.getName()

        setupProductsLayout()
        products_list.adapter = OrderDetailProductsAdapter(order.getProducts())

        order_place_name.text = order.getPlace().name

        // ui logic
        if (order.getStatus() != Order.STATUS.CANCELLED_STATUS || !order.getIdChat().isNullOrEmpty()) {
            order_actions_button.visibility = View.VISIBLE
        }
        if (order.isFavour()){
            val gratitudeLayout = findViewById<RelativeLayout>(R.id.rl_order_gratitude_points)
            gratitudeLayout.visibility = View.VISIBLE
            order_gratitude_points.text = order.getGratitudePoints().toString()
            isFavour = true
        }


        with(findViewById<Button>(R.id.order_actions_button)) {
            registerForContextMenu(this)
        }
    }

    fun onUpdateSuccess() {
        if (updateIsUnassign) {
            onBackPressed()
        } else {
            finish()
            startActivity(intent)
        }
    }

    fun onRateUserSuccess() {
        order?.setIsOwnerRated(true)
        order?.setIsDeliveryRated(true)
    }

    fun setUserData(user: User) {
        this.userType = user.type
        this.userId = user.id
    }

    fun createChat(order: Order?) {
        val chat = Chat(order?.getOwner()?.id!!, order.getDelivery()?.id!!, order.id)
        val handler = OrderDetailRequestHandler(this).forChatCreation()
        ChatService(handler).createChat(chat)
    }

    fun assignChat(chat: ChatFetched) {
        OrderService(OrderDetailRequestHandler(this)).assignChat(chat)
    }
}
