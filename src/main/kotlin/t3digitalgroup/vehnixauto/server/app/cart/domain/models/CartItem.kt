package t3digitalgroup.vehnixauto.server.app.cart.domain.models

import java.time.LocalDateTime

data class CartItem(
    val cartItemId: Long? = null,
    val userId: Long,
    val partType: String,
    val toolsId: Long,
    val quantity: Int,
    val unitPrice: String,
    val totalPrice: String,
    val devise: String = "USD",
    val isActive: Boolean = true,
    val paymentReference: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)

data class CartSummary(
    val items: List<CartItem>,
    val totalAmount: String,
    val currency: String,
    val itemCount: Int,
)
