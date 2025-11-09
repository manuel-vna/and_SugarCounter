package com.jumparoundcreations.sugarcounter.data

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    SPANISH("es"),
    GERMAN("de");

    companion object {
        fun fromCode(code: String): AppLanguage? =
            values().firstOrNull { it.code == code }
    }
}