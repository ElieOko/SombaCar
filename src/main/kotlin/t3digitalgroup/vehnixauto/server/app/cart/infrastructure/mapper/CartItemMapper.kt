package t3digitalgroup.vehnixauto.server.app.cart.infrastructure.mapper

import t3digitalgroup.vehnixauto.server.app.cart.domain.models.CartItem
import t3digitalgroup.vehnixauto.server.app.cart.infrastructure.entities.CartItemEntity

fun CartItemEntity.toDomain() = CartItem(
    cartItemId = this.cartItemId,
    userId = this.userId,
    partType = this.partType,
    toolsId = this.toolsId,
    quantity = this.quantity,
    unitPrice = this.unitPrice,
    totalPrice = this.totalPrice,
    devise = this.devise,
    isActive = this.isActive,
    paymentReference = this.paymentReference,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun CartItem.toEntity() = CartItemEntity(
    cartItemId = this.cartItemId,
    userId = this.userId,
    partType = this.partType,
    toolsId = this.toolsId,
    quantity = this.quantity,
    unitPrice = this.unitPrice,
    totalPrice = this.totalPrice,
    devise = this.devise,
    isActive = this.isActive,
    paymentReference = this.paymentReference,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)
