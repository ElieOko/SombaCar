package t3digitalgroup.vehnixauto.server.exception

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.http.*
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.utils.Mode
import java.time.LocalDateTime

@ControllerAdvice
@Profile(Mode.DEV)
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)
    @ExceptionHandler(Exception::class)
    fun handleGenericException(e : Exception): ResponseEntity<ErrorResponseDto>{
        logger.error("Handle exception", e)
        val errorDto = ErrorResponseDto(
            "Internal server error",
            e.message.toString(),
            LocalDateTime.now()
        )
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorDto)
    }

    @ExceptionHandler(value = [ResponseStatusException::class])
    fun handleStatusException(e: ResponseStatusException): ResponseEntity<MutableMap<String, Any>> {
        val map = mutableMapOf<String, Any>()
        map["message"] = e.reason.toString()
        return ResponseEntity
            .status(e.statusCode)
            .body(map)
    }
//
//    @ExceptionHandler(value = [NotFoundException::class])
//    fun handleEntityNotFound(e: NotFoundException): ResponseEntity<ErrorResponseDto>{
//        logger.error("Handle entityNotFoundException", e);
//        val errorDto = ErrorResponseDto(
//            "Entity not found",
//            e.message.toString(),
//            LocalDateTime.now()
//        )
//
//        return ResponseEntity
//            .status(HttpStatus.NOT_FOUND)
//            .body(errorDto)
//    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<MutableMap<String, Any>> {
        logger.error("Handle validation error", e)
        val mapList = mutableMapOf<String, Any>()
        e.bindingResult.fieldErrors.firstOrNull()?.let { error ->
            mapList["message"] = error.defaultMessage ?: "Validation"
        }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapList)
    }

    @ExceptionHandler(value = [IllegalArgumentException::class, IllegalStateException::class])
    fun handleBadRequest(e: Exception): ResponseEntity<MutableMap<String, Any>> {
        logger.error("Handle bad request", e)
        val mapList = mutableMapOf<String, Any>()
        mapList["message"] = e.message ?: "Bad request"
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapList)
    }

}