package t3digitalgroup.vehnixauto.server.exception

import java.time.LocalDateTime

data class ErrorResponseDto(
    val message : String,
    val detailMessage : String,
    val errorTime : LocalDateTime
)
