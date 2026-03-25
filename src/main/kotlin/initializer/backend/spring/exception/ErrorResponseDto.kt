package initializer.backend.spring.exception

import java.time.LocalDateTime

data class ErrorResponseDto(
    val message : String,
    val detailMessage : String,
    val errorTime : LocalDateTime
)
