package com.example.thinkpad.libra.data

import java.util.*

class Order(orderValue: Double = 10.0, orderId: Int = 0) {
    var productName: String = "Apple"

    var orderId: Int = 0

    var orderTime: Date = Date()

    var customerInfo: String = "DateBro"

    var orderValue: Double = 0.0

    init {
        this.orderValue = orderValue
        this.orderId = orderId
    }

}