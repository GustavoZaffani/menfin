package br.edu.utfpr.menfin.data.model

data class FeedbackModel(
    val _id: Int? = null,
    val userId: Int,
    val rating: Int,
    val comment: String,
    val timestamp: Long
)