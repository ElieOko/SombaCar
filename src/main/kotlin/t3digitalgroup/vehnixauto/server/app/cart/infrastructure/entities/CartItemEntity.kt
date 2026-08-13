package t3digitalgroup.vehnixauto.server.app.cart.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "cart_items")
class CartItemEntity(
    @Id
    @Column("id")
    val cartItemId: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("part_type")
    var partType: String,
    @Column("tools_id")
    var toolsId: Long,
    @Column("quantity")
    var quantity: Int,
    @Column("unit_price")
    var unitPrice: String,
    @Column("total_price")
    var totalPrice: String,
    @Column("devise")
    var devise: String = "USD",
    @Column("is_active")
    var isActive: Boolean = true,
    @Column("payment_reference")
    var paymentReference: String? = null,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
