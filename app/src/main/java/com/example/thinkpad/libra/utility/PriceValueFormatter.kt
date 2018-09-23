package com.example.thinkpad.libra.utility

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class PriceValueFormatter : IValueFormatter {

    private val myFormat: DecimalFormat = DecimalFormat("###,###,##0.0")

    override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {
        return " $" + myFormat.format(value)
    }
}