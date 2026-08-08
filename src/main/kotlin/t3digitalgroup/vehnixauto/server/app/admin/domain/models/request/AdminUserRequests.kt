package t3digitalgroup.vehnixauto.server.app.admin.domain.models.request

import jakarta.validation.constraints.NotNull

data class ChangeUserRoleRequest(
    @NotNull
    val accountId: Long,
)

data class UserLockRequest(
    @NotNull
    val locked: Boolean,
)
