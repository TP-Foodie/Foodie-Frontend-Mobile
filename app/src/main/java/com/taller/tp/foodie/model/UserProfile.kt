package com.taller.tp.foodie.model

data class UserProfile(
    val name: String, val last_name: String, val reputation: Float, val email: String,
    val profile_image: String, val sent_messages: Int, val deliveries_completed: Int,
    val phone: String
)

data class UserProfileFetched(
    val id: String,
    val name: String,
    val last_name: String,
    val reputation: Float,
    val email: String,
    val profile_image: String,
    val sent_messages: Int,
    val deliveries_completed: Int,
    val phone: String
)