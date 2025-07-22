package br.edu.utfpr.menfin.data.local

import kotlinx.serialization.Serializable

@Serializable
data class UserLogged(
    val id: Int = 0,
    val name: String = "",
    val user: String = ""
)