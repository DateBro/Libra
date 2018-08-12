package com.example.thinkpad.libra.data

class Product(productName: String = "Apple", productValue: Double = 6.11) {

    var productName: String = "Apple"

    var productValue: Double = 0.0

    init {
        this.productValue = productValue
        this.productName = productName
    }
}