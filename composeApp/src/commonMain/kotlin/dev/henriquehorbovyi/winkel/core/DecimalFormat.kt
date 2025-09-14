package dev.henriquehorbovyi.winkel.core

expect class DecimalFormat constructor() {

    fun decimalFormatSymbols(): FormatSymbols
    fun numberFormater(): NumberFormatter
}

abstract class NumberFormatter() {
    abstract fun format(value: Double): String
}

data class FormatSymbols(
    val decimalSeparator: Char,
    val groupingSeparator: Char,
    val zeroDigit: Char,
)

