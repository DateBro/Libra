package com.example.thinkpad.libra.data

/**
 * @author  Zhiyong Zhao
 */
class OrderDetail {

    var product = Product()
    var productWeight: String = "weight"
    var productTotalPay: String = "totalPay"

    constructor(product: Product, weight: String, totalPay: String) {
        this.product = product
        productWeight = weight
        productTotalPay = totalPay
    }

}