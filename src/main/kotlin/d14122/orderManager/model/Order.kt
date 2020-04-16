package d14122.orderManager.model

import java.util.*

class Order(val item: Item, val date: Date) {
    fun getTime() = date.time
}