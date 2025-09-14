package dev.henriquehorbovyi.winkel.core

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import java.text.DecimalFormat as DF

actual class DecimalFormat {
    actual fun decimalFormatSymbols(): FormatSymbols {
        val decimalFormatSymbols = DF().decimalFormatSymbols
        return FormatSymbols(
            decimalSeparator = decimalFormatSymbols.decimalSeparator,
            groupingSeparator = decimalFormatSymbols.groupingSeparator,
            zeroDigit = decimalFormatSymbols.zeroDigit
        )
    }

    actual fun numberFormater(): NumberFormatter = AndroidNumberFormatter()
}

class AndroidNumberFormatter(
    private val currency: Currency = Currency.getInstance(Locale.getDefault()),
    private val locale: Locale = Locale.getDefault(),
    private val formatter: NumberFormat = NumberFormat.getCurrencyInstance(locale)
) : NumberFormatter() {
    override fun format(value: Double): String {
        formatter.currency = currency
        return formatter.format(value)
    }
}