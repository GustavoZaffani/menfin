package br.edu.utfpr.menfin.data.model

data class UserModel(
    val _id: Int?,
    val name: String,
    val birthday: Long,
    val email: String,
    val user: String,
    val password: String
)