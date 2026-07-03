package t3digitalgroup.vehnixauto.server.app.notification.domain.models.request

import jakarta.validation.constraints.NotNull

data class NotificationRequest(
    @NotNull
    val title: String,
    @NotNull
    val message: String,
    @NotNull
    val tag: String,
)

data class NotificationSystemRequest(
    val title: String,
    val description: String,
)
