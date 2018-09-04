package com.example.thinkpad.libra.data

/**
 * @author  Zhiyong Zhao
 */
class Order(value: String = "totalPrice", id: String = "id",time:String = "Time") {

    var orderId:String = "id"
    var totalPrice: String = "totalPrice"
    var payTime: String = "payTime"

    init {
        totalPrice = value
        orderId = id
        payTime = time
    }

}