package com.example.thinkpad.libra.data

class Product {

    var productName: String = "Apple"
    var productValue: String = "$0.0"
    var productId: Int = 1

    constructor(name: String = "Apple", value: String = "$6.11") {
        productValue = value
        productName = name
    }

    constructor(name: String = "Apple", value: String = "$6.11", id: Int) : this(name, value) {
        productId = id
    }

}