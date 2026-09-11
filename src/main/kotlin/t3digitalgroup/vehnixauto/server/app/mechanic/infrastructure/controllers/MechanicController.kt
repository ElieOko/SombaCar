package t3digitalgroup.vehnixauto.server.app.mechanic.infrastructure.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.Validator
import kotlinx.coroutines.coroutineScope
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.mechanic.application.services.MechanicImageService
import t3digitalgroup.vehnixauto.server.app.mechanic.application.services.MechanicService
import t3digitalgroup.vehnixauto.server.app.mechanic.domain.models.request.MechanicContactRequestBody
import t3digitalgroup.vehnixauto.server.app.mechanic.domain.models.request.MechanicRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.mechanic.MechanicScope
import t3digitalgroup.vehnixauto.server.security.AdminAuthorization
import t3digitalgroup.vehnixauto.server.security.Auth
import t3digitalgroup.vehnixauto.server.security.PremiumAuthorization
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse
import t3digitalgroup.vehnixauto.server.utils.ApiResponseWithMessage
import t3digitalgroup.vehnixauto.server.utils.bufferMultipartFile
import tools.jackson.databind.json.JsonMapper

@Tag(name = "Mechanic", description = "Mécaniciens disponibles pour le dépannage (premium)")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class MechanicController(
    private val service: MechanicService,
    private val mechanicImageService: MechanicImageService,
    private val auth: Auth,
    private val premiumAuthorization: PremiumAuthorization,
    private val adminAuthorization: AdminAuthorization,
    private val sentry: SentryService,
    private val jsonMapper: JsonMapper,
    private val validator: Validator,
) {
    @Operation(summary = "Créer un mécanicien (admin)")
    @PostMapping(
        MechanicScope.PROTECTED,
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    suspend fun create(
        request: HttpServletRequest,
        @PathVariable version: String,
        multipartRequest: MultipartHttpServletRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            adminAuthorization.requireAdmin()
            val body = parseMechanicRequest(resolveMechanicJson(multipartRequest))
            val bufferedImages = multipartRequest.getFiles("images")
                .filter { !it.isEmpty }
                .map(::bufferMultipartFile)
            val mechanic = service.create(body)
            bufferedImages.forEach { file ->
                mechanicImageService.createFromFile(mechanic.mechanicId!!, file)
            }
            ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWithMessage(
                    data = service.findById(mechanic.mechanicId!!),
                    message = "Saved ${bufferedImages.size} images",
                ),
            )
        } finally {
            recordMetric(request, startNanos, "api.mechanic.create")
        }
    }

    @Operation(summary = "Ajouter des images à un mécanicien (admin)")
    @PostMapping(
        "${MechanicScope.PROTECTED}/{id}/images",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    suspend fun addImages(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        multipartRequest: MultipartHttpServletRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            adminAuthorization.requireAdmin()
            val files = multipartRequest.getFiles("images")
                .filter { !it.isEmpty }
                .map(::bufferMultipartFile)
            val mechanic = service.addImages(id, files)
            ResponseEntity.ok(
                ApiResponseWithMessage(
                    data = mechanic,
                    message = "Saved ${files.size} images",
                ),
            )
        } finally {
            recordMetric(request, startNanos, "api.mechanic.addimages")
        }
    }

    @Operation(summary = "Mécaniciens disponibles la nuit (premium)")
    @GetMapping("${MechanicScope.PROTECTED}/night-available", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findNightAvailable(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            premiumAuthorization.requirePremium()
            ApiResponse(service.findNightAvailable())
        } finally {
            recordMetric(request, startNanos, "api.mechanic.nightavailable")
        }
    }

    @Operation(summary = "Contacter un mécanicien pour dépannage (premium)")
    @PostMapping("${MechanicScope.PROTECTED}/contact", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun contact(
        request: HttpServletRequest,
        @PathVariable version: String,
        @Valid @RequestBody body: MechanicContactRequestBody,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = premiumAuthorization.requirePremium()
            ResponseEntity.status(HttpStatus.CREATED).body(service.contact(userId, body))
        } finally {
            recordMetric(request, startNanos, "api.mechanic.contact")
        }
    }

    @Operation(summary = "Mes demandes de dépannage")
    @GetMapping("${MechanicScope.PROTECTED}/contacts/mine", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findMyContacts(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            val userId = requireUserId()
            ApiResponse(service.findContactsByUser(userId))
        } finally {
            recordMetric(request, startNanos, "api.mechanic.contacts")
        }
    }

    private fun parseMechanicRequest(mechanicJson: String): MechanicRequest {
        val body = try {
            jsonMapper.readValue(mechanicJson, MechanicRequest::class.java)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid mechanic JSON: ${e.message}")
        }
        val violation = validator.validate(body).firstOrNull()
        if (violation != null) {
            throw IllegalArgumentException(violation.message ?: "Invalid mechanic data")
        }
        return body
    }

    private fun resolveMechanicJson(multipartRequest: MultipartHttpServletRequest): String {
        multipartRequest.getParameter("mechanic")?.takeIf { it.isNotBlank() }?.let { return it }
        val mechanicPart = multipartRequest.getFile("mechanic")
            ?: multipartRequest.getFiles("mechanic").firstOrNull()
        if (mechanicPart != null && !mechanicPart.isEmpty) {
            return String(mechanicPart.bytes, Charsets.UTF_8)
        }
        throw IllegalArgumentException("Missing mechanic field")
    }

    private suspend fun requireUserId(): Long =
        auth.user()?.first?.userId ?: throw ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Authentification requise",
        )

    private fun recordMetric(request: HttpServletRequest, startNanos: Long, metricName: String) {
        sentry.callToMetric(
            MetricModel(
                startNanos = startNanos,
                status = "200",
                route = "${request.method} /${request.requestURI}",
                countName = "$metricName.count",
                distributionName = "$metricName.latency",
            ),
        )
    }
}
