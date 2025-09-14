package dev.henriquehorbovyi.winkel.core

import platform.Foundation.NSLocaleDecimalSeparator

actual class DecimalFormat {

    actual fun decimalFormatSymbols(): FormatSymbols {
        return FormatSymbols( decimalSeparator = NSLocaleDecimalSeparator.toString()[0], ',', '0')
    }

    actual fun numberFormater(): NumberFormatter = IOSNumberFormatter()
}
class IOSNumberFormatter : NumberFormatter() {
    override fun format(value: Double): String {
        TODO("Not yet implemented")
    }

}