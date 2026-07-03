package t3digitalgroup.vehnixauto.server.app.message.domain.models.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CarListingThreadRequest(
    @NotNull
    val carListingId: Long,
    @NotNull
    val buyerId: Long,
    @NotBlank
    val initialMessage: String,
)

data class CarListingMessageRequest(
    @NotNull
    val threadId: Long,
    @NotNull
    val senderId: Long,
    @NotBlank
    val content: String,
)
