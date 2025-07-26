package br.edu.utfpr.menfin.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Long.toBrazilianDateFormat(): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(Date(this))
}

fun Long.formatTimestamp(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}