package initializer.backend.spring.app.message.domain.models.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SupportThreadRequest(
    @NotNull
    val userId: Long,
    @NotBlank
    val subject: String,
    @NotBlank
    val partSought: String,
    val partReference: String? = null,
    val compatibleBrand: String? = null,
    val compatibleModel: String? = null,
    @NotBlank
    val initialMessage: String
)

data class SupportMessageRequest(
    @NotNull
    val threadId: Long,
    @NotBlank
    val content: String,
    val senderId: Long? = null
)

data class PlatformReplyRequest(
    @NotNull
    val threadId: Long,
    @NotNull
    val adminId: Long,
    @NotBlank
    val content: String
)
