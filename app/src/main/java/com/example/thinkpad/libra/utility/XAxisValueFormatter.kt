package com.example.thinkpad.libra.utility

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter

class XAxisValueFormatter(values: Array<String>) : IAxisValueFormatter {

    private var mValues: Array<String> = values

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        // "value" represents the position of the label on the axis (x or y)
        return mValues[value.toInt()]
    }

    /** this is only needed if numbers are returned, else return 0  */
    fun getDecimalDigits(): Int {
        return 0
    }
}