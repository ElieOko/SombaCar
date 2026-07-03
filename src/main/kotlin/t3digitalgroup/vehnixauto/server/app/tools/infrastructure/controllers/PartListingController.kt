package t3digitalgroup.vehnixauto.server.app.tools.infrastructure.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Validator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartHttpServletRequest
import t3digitalgroup.vehnixauto.server.app.tools.application.services.*
import t3digitalgroup.vehnixauto.server.app.tools.domain.models.request.PartListingRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.tools.PartListingScope
import t3digitalgroup.vehnixauto.server.security.monitoring.*
import t3digitalgroup.vehnixauto.server.utils.ApiResponse
import t3digitalgroup.vehnixauto.server.utils.ApiResponseWithMessage
import t3digitalgroup.vehnixauto.server.utils.ListingStatus
import t3digitalgroup.vehnixauto.server.utils.ListingType
import t3digitalgroup.vehnixauto.server.utils.bufferMultipartFile
import tools.jackson.databind.json.JsonMapper

@Tag(name = "Part Listing", description = "Gestion des annonces de pièces détachées")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class PartListingController(
    private val service: PartListingService,
    private val partImageService: PartImageService,
    private val sentry: SentryService,
    private val jsonMapper: JsonMapper,
    private val validator: Validator,
) {
    @Operation(summary = "Créer une annonce de pièce")
    @PostMapping(
        PartListingScope.PROTECTED,
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
            val listingJson = resolveListingJson(multipartRequest)
            val body = parseListingRequest(listingJson)
            val bufferedImages = multipartRequest.getFiles("images")
                .filter { !it.isEmpty }
                .map(::bufferMultipartFile)
            val part = service.create(body)
            bufferedImages.forEach { file ->
                partImageService.createFromFile(part.partListingId!!, file)
            }
            ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWithMessage(data = part, message = "Saved ${bufferedImages.size} images")
            )
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.partlisting.create.count",
                    distributionName = "api.partlisting.create.latency",
                )
            )
        }
    }

    private fun parseListingRequest(listingJson: String): PartListingRequest {
        val body = try {
            jsonMapper.readValue(listingJson, PartListingRequest::class.java)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid listing JSON: ${e.message}")
        }
        val violation = validator.validate(body).firstOrNull()
        if (violation != null) {
            throw IllegalArgumentException(violation.message ?: "Invalid listing data")
        }
        return body
    }

    private fun resolveListingJson(multipartRequest: MultipartHttpServletRequest): String {
        multipartRequest.getParameter("listing")?.takeIf { it.isNotBlank() }?.let { return it }
        val listingPart = multipartRequest.getFile("listing")
            ?: multipartRequest.getFiles("listing").firstOrNull()
        if (listingPart != null && !listingPart.isEmpty) {
            return String(listingPart.bytes, Charsets.UTF_8)
        }
        throw IllegalArgumentException("Missing listing field")
    }

    @Operation(summary = "Détail d'une annonce de pièce")
    @GetMapping("${PartListingScope.PUBLIC}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findById(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(service.findById(id))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.partlisting.findbyid.count",
                    distributionName = "api.partlisting.findbyid.latency",
                )
            )
        }
    }

    @Operation(summary = "Annonces de pièces d'un utilisateur")
    @GetMapping("${PartListingScope.PROTECTED}/user/{userId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByUserId(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable userId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByUserId(userId).toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.partlisting.findbyuserid.count",
                    distributionName = "api.partlisting.findbyuserid.latency",
                )
            )
        }
    }

    @Operation(summary = "Annonces de pièces par type")
    @GetMapping("${PartListingScope.PUBLIC}/type/{listingType}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByListingType(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable listingType: ListingType,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByListingType(listingType).toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.partlisting.findbylistingtype.count",
                    distributionName = "api.partlisting.findbylistingtype.latency",
                )
            )
        }
    }

    @Operation(summary = "Rechercher des annonces de pièces")
    @GetMapping("${PartListingScope.PUBLIC}/search", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun search(
        request: HttpServletRequest,
        @PathVariable version: String,
        @RequestParam query: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.search(query).toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.partlisting.search.count",
                    distributionName = "api.partlisting.search.latency",
                )
            )
        }
    }

    @Operation(summary = "Mettre à jour le statut d'une annonce de pièce")
    @PatchMapping("${PartListingScope.PROTECTED}/{id}/status", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun updateStatus(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @RequestParam status: ListingStatus,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(service.updateStatus(id, status))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.partlisting.updatestatus.count",
                    distributionName = "api.partlisting.updatestatus.latency",
                )
            )
        }
    }
}
