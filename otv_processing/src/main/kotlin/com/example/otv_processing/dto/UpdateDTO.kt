package com.example.otv_processing.dto

data class UpdateDTO(
    var message: String,
    val userTelegramName: String,
    val chatId: Long
)
