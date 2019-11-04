package com.taller.tp.foodie.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ChatFetched
import com.taller.tp.foodie.model.ChatMessage
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.UserProfileFetched
import com.taller.tp.foodie.model.requestHandlers.*
import com.taller.tp.foodie.services.ChatService
import com.taller.tp.foodie.services.ProfileService
import com.taller.tp.foodie.ui.ui_adapters.ChatAdapter
import kotlinx.android.synthetic.main.activity_chat.*
import java.lang.ref.WeakReference
import java.util.*

class ChatActivity : AppCompatActivity() {

    private var chatId: String? = null
    private var chat: ChatFetched? = null
    private var messagesList = mutableListOf<ChatMessage>()
    private var myData: UserProfileFetched? = null
    private var otherData: UserProfileFetched? = null

    private var chatAdapter: ChatAdapter? = null

    companion object {
        const val CHAT_ID = "chatId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatId = intent.getStringExtra(CHAT_ID)

        if (!checkPreconditionsAreMet()) {
            ErrorHandler.handleError(chat_layout)
            return
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, IntentFilter("newMessage")
        )

        persistChatState(true)

        setupClickListeners()

        setupUI()

        getChat()
    }

    private fun setupClickListeners() {
        btn_send_message.setOnClickListener {
            val message = message_text.text.toString().trim()

            val cal = Calendar.getInstance()
            val messageData = ChatMessage(myData?.id!!, message, cal.timeInMillis, chatId!!)

            messagesList.add(0, messageData)

            ChatService(applicationContext, SendMessageRequestHandler(this))
                .sendMessage(chatId!!, messageData)

            message_text.setText("")
            btn_send_message.isEnabled = false
        }
    }

    private fun checkPreconditionsAreMet(): Boolean {
        if (chatId == null) {
            return false
        }
        return true
    }

    fun updateChatMessages(chatMessages: MutableList<ChatMessage>) {
        messagesList = chatMessages

        updateChatMessagesUI()
    }

    fun updateChat(chatMessages: ChatFetched) {
        chat = chatMessages

        getMyData()
    }

    fun updateMyData(userProfile: UserProfileFetched) {
        myData = userProfile

        getOtherData()
    }

    fun updateOtherData(userProfile: UserProfileFetched) {
        otherData = userProfile

        setupChatToolbar()

        getChatMessages()
    }

    private fun getMyData() {
        ProfileService(applicationContext, GetUserForChatRequestHandler(WeakReference(this)))
            .getUserForChat()
    }

    private fun getOtherData() {
        val otherUserId = if (chat?.uid_1 == myData?.id) {
            chat?.uid_2!!
        } else {
            chat?.uid_1!!
        }

        ProfileService(applicationContext, GetOtherUserForChatRequestHandler(WeakReference(this)))
            .getOtherUserForChat(otherUserId)
    }

    private fun getChat() {
        ChatService(applicationContext, GetChatRequestHandler(this))
            .getChat(chatId!!)
    }

    private fun getChatMessages() {
        ChatService(applicationContext, ListChatMessagesRequestHandler(this))
            .getChatMessages(chatId!!)
    }

    private fun setupUI() {
        val manager = LinearLayoutManager(applicationContext)
        manager.reverseLayout = true
        messages_list.layoutManager = manager
    }

    @SuppressLint("SetTextI18n")
    private fun setupChatToolbar() {
        user_name.text = otherData?.name + " " + otherData?.last_name
    }

    private fun updateChatMessagesUI() {
        // update messages in messages list
        chatAdapter = ChatAdapter(messagesList, myData?.id!!)
        messages_list.adapter = chatAdapter

        messages_list.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            messages_list.smoothScrollToPosition(0)
        }
    }

    override fun onStop() {
        super.onStop()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            mMessageReceiver
        )

        persistChatState(false)
    }

    override fun onRestart() {
        super.onRestart()

        if (!checkPreconditionsAreMet()) {
            ErrorHandler.handleError(chat_layout)
            return
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, IntentFilter("newMessage")
        )

        persistChatState(true)

        setupUI()

        getChat()
    }


    fun chatMessageSentUpdateUI() {
        btn_send_message.isEnabled = true
        updateChatMessagesUI()
    }

    fun chatMessageNotSentUpdateUI() {
        btn_send_message.isEnabled = true
        messagesList.removeAt(0)
    }

    private fun persistChatState(isInForeground: Boolean) {
        val pref = applicationContext.getSharedPreferences("ChatId", 0)
        val editor = pref.edit()
        if (isInForeground) {
            editor.putString("CId", chatId)
        } else {
            editor.remove("CId")
        }
        editor.apply()
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e("ChatActivity", "Local Broadcast RECEIVED")
            val uidSender = intent.getStringExtra("uid_sender")
            val message = intent.getStringExtra("message")
            val timestamp = intent.getLongExtra("timestamp", Calendar.getInstance().timeInMillis)
            val idChat = intent.getStringExtra("chat_id")
            val chatMessage = ChatMessage(uidSender, message, timestamp, idChat)
            messagesList.add(0, chatMessage)
            runOnUiThread {
                updateChatMessagesUI()
            }
        }
    }
}
