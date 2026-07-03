package t3digitalgroup.vehnixauto.server.app.car.infrastructure.controllers

import tools.jackson.databind.json.JsonMapper
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
import t3digitalgroup.vehnixauto.server.app.car.application.services.*
import t3digitalgroup.vehnixauto.server.app.car.domain.models.request.CarListingRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.car.CarListingScope
import t3digitalgroup.vehnixauto.server.security.monitoring.*
import t3digitalgroup.vehnixauto.server.utils.*
import t3digitalgroup.vehnixauto.server.utils.bufferMultipartFile

@Tag(name = "Car Listing", description = "Gestion des annonces de voitures")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class CarListingController(
    private val service: CarListingService,
    private val carImageService: CarImageService,
    private val sentry: SentryService,
    private val jsonMapper: JsonMapper,
    private val validator: Validator,
) {
    @Operation(summary = "Créer une annonce de voiture")
    @PostMapping(
        CarListingScope.PUBLIC,
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
            val car = service.create(body)
            bufferedImages.forEach { file ->
                carImageService.createFromFile(car.listingId!!, file)
            }
            ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWithMessage(data = car, message = "Saved ${bufferedImages.size} images")
            )
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.carlisting.create.count",
                    distributionName = "api.carlisting.create.latency",
                )
            )
        }
    }

    private fun parseListingRequest(listingJson: String): CarListingRequest {
        val body = try {
            jsonMapper.readValue(listingJson, CarListingRequest::class.java)
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

    @Operation(summary = "Détail d'une annonce de voiture")
    @GetMapping("${CarListingScope.PUBLIC}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
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
                    countName = "api.carlisting.findbyid.count",
                    distributionName = "api.carlisting.findbyid.latency",
                )
            )
        }
    }

    @Operation(summary = "Annonces d'un utilisateur")
    @GetMapping("${CarListingScope.PROTECTED}/user/{userId}", produces = [MediaType.APPLICATION_JSON_VALUE])
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
                    countName = "api.carlisting.findbyuserid.count",
                    distributionName = "api.carlisting.findbyuserid.latency",
                )
            )
        }
    }

    @Operation(summary = "Annonces par type (vente, location, échange)")
    @GetMapping("${CarListingScope.PUBLIC}/type/{listingType}", produces = [MediaType.APPLICATION_JSON_VALUE])
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
                    countName = "api.carlisting.findbylistingtype.count",
                    distributionName = "api.carlisting.findbylistingtype.latency",
                )
            )
        }
    }

    @Operation(summary = "Annonces par modèle de voiture")
    @GetMapping("${CarListingScope.PUBLIC}/model/{carModelId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByCarModelId(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable carModelId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByCarModelId(carModelId).toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.carlisting.findbycarmodelid.count",
                    distributionName = "api.carlisting.findbycarmodelid.latency",
                )
            )
        }
    }

    @Operation(summary = "Annonces par motorisation et état")
    @GetMapping("${CarListingScope.PUBLIC}/filter", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByElectricAndCondition(
        request: HttpServletRequest,
        @PathVariable version: String,
        @RequestParam isElectric: Boolean,
        @RequestParam condition: ItemCondition,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByElectricAndCondition(isElectric, condition).toList())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.carlisting.findbyelectricandcondition.count",
                    distributionName = "api.carlisting.findbyelectricandcondition.latency",
                )
            )
        }
    }

    @Operation(summary = "Mettre à jour le statut d'une annonce")
    @PatchMapping("${CarListingScope.PROTECTED}/{id}/status", produces = [MediaType.APPLICATION_JSON_VALUE])
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
                    countName = "api.carlisting.updatestatus.count",
                    distributionName = "api.carlisting.updatestatus.latency",
                )
            )
        }
    }
}
