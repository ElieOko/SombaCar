package t3digitalgroup.vehnixauto.server.app.cart.domain.models.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class CartItemRequest(
    @NotNull
    val toolsId: Long,
    @NotNull
    @Min(1)
    val quantity: Int,
)

data class CartItemUpdateRequest(
    @NotNull
    @Min(1)
    val quantity: Int,
)
