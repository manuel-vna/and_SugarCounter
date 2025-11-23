package com.jumparoundcreations.sugarcounter.ui.utils

class InputFilters {

    companion object {

        fun filterDecimalOneDigit1to100(input: String): Boolean {
            return true
            //val regex = Regex("^(100(\\.0)?|([1-9]?[0-9](\\.[0-9])?))\$")
            //return regex.matches(input)
        }

    }
}