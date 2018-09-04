package com.example.thinkpad.libra.data

/**
 * @author  Zhiyong Zhao
 */
class Product {

    var productName: String = "productName"
    var productValue: String = "productValue"
    var productId: Int = 1

    constructor(name: String = "Apple", value: String = "$6.11") {
        productValue = value
        productName = name
    }

    constructor(name: String = "Apple", value: String = "$6.11", id: Int) : this(name, value) {
        productId = id
    }

}