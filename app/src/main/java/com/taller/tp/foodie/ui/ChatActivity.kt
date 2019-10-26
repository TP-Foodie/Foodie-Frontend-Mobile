package com.taller.tp.foodie.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.taller.tp.foodie.R
import com.taller.tp.foodie.model.Chat
import com.taller.tp.foodie.model.ChatMessage
import com.taller.tp.foodie.model.ErrorHandler
import com.taller.tp.foodie.model.UserProfile
import com.taller.tp.foodie.model.requestHandlers.GetChatRequestHandler
import com.taller.tp.foodie.model.requestHandlers.SendMessageRequestHandler
import com.taller.tp.foodie.services.ChatService
import com.taller.tp.foodie.ui.ui_adapters.ChatAdapter
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*

class ChatActivity : AppCompatActivity() {

    private var chatId: String? = null
    private var chat: Chat? = null
    private var messagesList = mutableListOf<ChatMessage>()
    private var myData: UserProfile? = null
    private var otherData: UserProfile? = null

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

            // TODO: my_uid
            val cal = Calendar.getInstance()
            val messageData = ChatMessage("my_uid", message, cal.timeInMillis)
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

    fun updateChat(chatMessages: Chat) {
        chat = chatMessages

        setupData()
    }

    fun updateMyData(userProfile: UserProfile) {
        myData = userProfile
    }

    fun updateOtherData(userProfile: UserProfile) {
        otherData = userProfile

        setupChatToolbar()
    }

    private fun getMyData() {
        // get my data con user service (o profile service)
    }

    private fun getOtherData() {
        // get other data con user service (o profile service)
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
        // TODO: my uid
        chatAdapter = ChatAdapter(messagesList, "my_uid")
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
