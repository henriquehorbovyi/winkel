package dev.henriquehorbovyi.winkel.core


class MoneyConverter(
    private val decimalFormat: DecimalFormat,
    private val formatSymbols: FormatSymbols = decimalFormat.decimalFormatSymbols(),
    private val numberFormater: NumberFormatter = decimalFormat.numberFormater()
) {
    fun convertToDecimal(value: String): Double {
        return try {
            val strValue = value
                .replace("[^\\d${formatSymbols.decimalSeparator}]".toRegex(), "")
            val number = strValue.replace(formatSymbols.decimalSeparator, '.')
            number.toDouble() / 100.0
        } catch (_: Exception) {
            0.0
        }
    }

    fun decimalToCents(value: Double): Int = (value * 100).toInt()

    fun formatAsCurrency(value: Double): String = numberFormater.format(value)
}
