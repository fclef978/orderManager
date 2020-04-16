package d14122.orderManager.model

import java.io.Serializable

open class Item(val name: String, val price: Double): Serializable {
    private val serialVersionUID = 1L
}