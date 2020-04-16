package d14122.orderManager.database

import d14122.orderManager.model.Order
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.util.*
import javax.persistence.*

@Entity
@Data
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int?

    @Column(nullable = false)
    val name: String

    @Column(nullable=false)
    val date: Date?

    constructor(id: Int, taste: String, date: Date) {
        this.id = id
        this.name = taste
        this.date = date
    }

    constructor() {
        id = null
        name = ""
        date = null
    }

    constructor(order: Order) {
        id = null
        name = order.item.name
        date = order.date
    }
}