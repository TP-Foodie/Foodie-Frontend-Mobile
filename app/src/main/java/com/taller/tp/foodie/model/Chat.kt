package com.taller.tp.foodie.model

data class Chat(var uid_1: String, var uid_2: String, var id_order: String)

data class ChatFetched(var id: String, var uid_1: String, var uid_2: String, var id_order: String)