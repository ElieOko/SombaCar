package t3digitalgroup.vehnixauto.server.app.message.domain.models.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class PartListingThreadRequest(
    @NotNull
    val partListingId: Long,
    @NotNull
    val buyerId: Long,
    @NotBlank
    val initialMessage: String,
)

data class PartListingMessageRequest(
    @NotNull
    val threadId: Long,
    @NotNull
    val senderId: Long,
    val content: String? = null,
)
