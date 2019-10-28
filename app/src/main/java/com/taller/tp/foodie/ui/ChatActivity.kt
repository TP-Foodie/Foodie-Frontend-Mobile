package com.taller.tp.foodie.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.ChatFetched
import com.taller.tp.foodie.model.ChatMessage
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.UserProfileFetched
import com.taller.tp.foodie.model.requestHandlers.GetChatRequestHandler
import com.taller.tp.foodie.model.requestHandlers.GetOtherUserForChatRequestHandler
import com.taller.tp.foodie.model.requestHandlers.GetUserForChatRequestHandler
import com.taller.tp.foodie.model.requestHandlers.SendMessageRequestHandler
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

        setupClickListeners()

        setupUI()

        getChat()
    }

    private fun setupClickListeners() {
        btn_send_message.setOnClickListener {
            val message = message_text.text.toString().trim()

            val cal = Calendar.getInstance()
            val messageData = ChatMessage(myData?.id!!, message, cal.timeInMillis, chatId!!)
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

    private fun setupData() {
        getMyData()

        getOtherData()

        getChatMessages()
    }

    fun updateChatMessages(chatMessages: MutableList<ChatMessage>) {
        messagesList = chatMessages

        updateChatMessagesUI()
    }

    fun updateChat(chatMessages: ChatFetched) {
        chat = chatMessages

        setupData()
    }

    fun updateMyData(userProfile: UserProfileFetched) {
        myData = userProfile
    }

    fun updateOtherData(userProfile: UserProfileFetched) {
        otherData = userProfile

        setupChatToolbar()
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
        ChatService(applicationContext, GetChatRequestHandler(this))
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

    override fun onResume() {
        super.onResume()

        if (!checkPreconditionsAreMet()) {
            ErrorHandler.handleError(chat_layout)
            return
        }

        setupUI()

        getChat()
    }

    fun chatMessageSentUpdateUI() {
        btn_send_message.isEnabled = true
    }
}
