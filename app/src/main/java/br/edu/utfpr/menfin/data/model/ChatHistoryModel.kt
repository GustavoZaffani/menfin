package br.edu.utfpr.menfin.data.model

enum class Sender {
    USER, MENTOR, SYSTEM_LOADER
}

data class ChatHistoryModel(
    val _id: Int? = null,
    val userId: Int,
    val text: String,
    val sender: Sender,
    val timestamp: Long
)