package com.example.thinkpad.libra.utility

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.DecimalFormat

class YAxisValueFormatter :IAxisValueFormatter {

    // format values to 1 decimal digit
    private val myFormat: DecimalFormat = DecimalFormat ("###,###,##0.0")

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        // "value" represents the position of the label on the axis (x or y)
        return  " $" + myFormat.format(value)
    }

    /** this is only needed if numbers are returned, else return 0 */
    fun getDecimalDigits(): Int {
        return 1
    }
}