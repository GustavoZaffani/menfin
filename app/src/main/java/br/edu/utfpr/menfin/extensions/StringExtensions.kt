package br.edu.utfpr.menfin.extensions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun String.toMillisFromBrazilianDateFormat(): Long? {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    format.timeZone = TimeZone.getTimeZone("UTC")
    return try {
        val date: Date? = format.parse(this)
        date?.time
    } catch (e: ParseException) {
        null
    }
}