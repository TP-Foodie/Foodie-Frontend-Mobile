package com.taller.tp.foodie.model

data class ChatMessage(
    var uid_sender: String, var message: String, var timestamp: Long,
    var id_chat: String
)