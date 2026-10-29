package com.pe.tracker.util

object PhoneParser {

    fun isValid(raw: String): Boolean {
        val digits = raw.filter { it.isDigit() }
        return digits.length in 8..15
    }

    fun formatDisplay(raw: String): String {
        val digits = raw.filter { it.isDigit() }
        return when (digits.length) {
            9 -> "${digits.take(3)}-${digits.drop(3)}"       // Perú
            10 -> "(${digits.take(3)}) ${digits.substring(3,6)}-${digits.substring(6)}" // US
            else -> raw
        }
    }

    fun extractCountryCode(raw: String): String {
        val digits = raw.filter { it.isDigit() }
        return when {
            digits.startsWith("51") -> "PE"
            digits.startsWith("52") -> "MX"
            digits.startsWith("54") -> "AR"
            digits.startsWith("57") -> "CO"
            digits.startsWith("56") -> "CL"
            digits.startsWith("34") -> "ES"
            else -> "???"
        }
    }
}
