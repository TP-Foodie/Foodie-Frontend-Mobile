package com.taller.tp.foodie.services

import com.android.volley.Response
import com.taller.tp.foodie.model.Chat
import com.taller.tp.foodie.model.ChatMessage
import com.taller.tp.foodie.model.requestHandlers.RequestHandler
import org.json.JSONObject

class ChatService(private val requestHandler: RequestHandler) {

    private val client = BackService.getInstance()

    companion object {
        // endpoint
        const val CHATS_RESOURCE = "/chats/"
        const val MESSAGES_RESOURCE = "/messages/"

        // chat fields
        const val USER_1_ID_FIELD = "uid_1"
        const val USER_2_ID_FIELD = "uid_2"
        const val ORDER_ID_FIELD = "id_order"

        // chat message fields
        const val SENDER_ID_FIELD = "uid_sender"
        const val MESSAGE_FIELD = "message"
        const val TIMESTAMP_FIELD = "timestamp"
    }

    fun createChat(chat: Chat) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { response ->
            requestHandler.onSuccess(response)
        }
        val errorListener = Response.ErrorListener { error ->
            requestHandler.onError(error)
        }

        val request = JSONObject()
        request.put(USER_1_ID_FIELD, chat.uid_1)
        request.put(USER_2_ID_FIELD, chat.uid_2)
        request.put(ORDER_ID_FIELD, chat.id_order)

        client.doPost(CHATS_RESOURCE, listener, request, errorListener)
    }

    fun getChat(chatId: String) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { response ->
            requestHandler.onSuccess(response)
        }
        val errorListener = Response.ErrorListener { error ->
            requestHandler.onError(error)
        }

        client.doGetObject(CHATS_RESOURCE + chatId, listener, errorListener)
    }

    fun getChatMessages(chatId: String) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { response ->
            requestHandler.onSuccess(response)
        }
        val errorListener = Response.ErrorListener { error ->
            requestHandler.onError(error)
        }

        client.doGetObject(CHATS_RESOURCE + chatId + MESSAGES_RESOURCE, listener, errorListener)
    }

    fun sendMessage(chatId: String, messageData: ChatMessage) {
        requestHandler.begin()

        val listener = Response.Listener<JSONObject> { response ->
            requestHandler.onSuccess(response)
        }
        val errorListener = Response.ErrorListener { error ->
            requestHandler.onError(error)
        }

        val request = JSONObject()
        request.put(SENDER_ID_FIELD, messageData.uid_sender)
        request.put(MESSAGE_FIELD, messageData.message)
        request.put(TIMESTAMP_FIELD, messageData.timestamp)

        client.doPost(
            CHATS_RESOURCE + chatId + MESSAGES_RESOURCE, listener,
            request, errorListener
        )
    }
}