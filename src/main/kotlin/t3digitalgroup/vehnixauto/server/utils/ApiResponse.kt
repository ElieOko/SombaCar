package t3digitalgroup.vehnixauto.server.utils

data class ApiResponseWithMessage<T>(
    val message: String,
    val data: T
)

data class ApiResponse<T>(
    val data: T
)
