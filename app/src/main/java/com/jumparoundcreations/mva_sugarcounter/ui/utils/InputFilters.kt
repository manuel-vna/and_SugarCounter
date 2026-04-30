package com.jumparoundcreations.mva_sugarcounter.ui.utils

class InputFilters {

    companion object {

        @Suppress("ReturnCount")
        fun filterBlockingOverHundred(input: String): Boolean {
            val regex = Regex("^(?:|[0-9]([0-9]?([.,]?[0-9]?)?)?)$")

            if (!regex.matches(input)) return false
            if (input.isBlank()) return true  // allow deleting everything

            // Convert comma → dot
            val normalized = input.replace(',', '.')

            // Partial inputs like "10." or "10," should be allowed
            if (normalized.last() == '.') return true
            if (normalized == "100.") return true  // allow typing 100.

            // Full number validation
            val number = normalized.toDoubleOrNull() ?: return false
            return number <= 100.0
        }

        @Suppress("ReturnCount")
        fun filterBlockingOverThousand(input: String): Boolean {
            val regex = Regex("^([0-9]{0,3}([.,][0-9]?)?)$")

            if (!regex.matches(input)) return false
            if (input.isBlank()) return true  // allow deleting everything

            // Convert comma → dot
            val normalized = input.replace(',', '.')

            // Partial inputs like "10." or "10," should be allowed
            if (normalized.last() == '.') return true
            if (normalized == "1000.") return true  // allow typing 100.

            // Full number validation
            val number = normalized.toDoubleOrNull() ?: return false
            return number <= 1000.0
        }


    }
}