package com.example.thinkpad.libra.data

import java.util.*

class Order(orderValue: Double = 10.0) {
    var productName: String = "Apple"

    var orderTime: Date = Date()

    var customerInfo: String = "DateBro"

    var orderValue: Double = 0.0

    init {
        this.orderValue = orderValue
    }

    fun Order() {
        orderValue = 12.0
    }

    fun Order(customerInfo: String) {
        this.customerInfo = customerInfo
    }

}