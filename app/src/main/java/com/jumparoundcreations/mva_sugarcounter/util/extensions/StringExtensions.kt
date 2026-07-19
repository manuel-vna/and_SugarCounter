package com.jumparoundcreations.mva_sugarcounter.util.extensions

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

fun String.toDoubleFormattedOrNull(): Double? = this.replace(",", ".").toDoubleOrNull()

fun String.formatDateForDisplay(): String =
    try {
        val input = LocalDate.parse(this)
        val formatter = DateTimeFormatter.ofPattern("EEEE (dd.MM.)")
        input.format(formatter)
    } catch (e: Exception) {
        println(e)
        this
    }

fun String.yearMonthFromIsoDate(): YearMonth {
    val ld = LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)
    return YearMonth.of(ld.year, ld.month)
}