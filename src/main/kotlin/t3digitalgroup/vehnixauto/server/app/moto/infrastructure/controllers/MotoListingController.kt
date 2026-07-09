package t3digitalgroup.vehnixauto.server.app.moto.infrastructure.controllers

import tools.jackson.databind.json.JsonMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Validator
import kotlinx.coroutines.coroutineScope
import org.springframework.context.annotation.Profile
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartHttpServletRequest
import t3digitalgroup.vehnixauto.server.app.moto.application.services.MotoImageService
import t3digitalgroup.vehnixauto.server.app.moto.application.services.MotoListingService
import t3digitalgroup.vehnixauto.server.app.moto.domain.models.request.MotoListingRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.moto.MotoListingScope
import t3digitalgroup.vehnixauto.server.security.monitoring.*
import t3digitalgroup.vehnixauto.server.utils.*
import t3digitalgroup.vehnixauto.server.utils.bufferMultipartFile

@Tag(name = "Moto Listing", description = "Gestion des annonces de motos")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class MotoListingController(
    private val service: MotoListingService,
    private val motoImageService: MotoImageService,
    private val sentry: SentryService,
    private val jsonMapper: JsonMapper,
    private val validator: Validator,
) {
    @Operation(summary = "Créer une annonce de moto")
    @PostMapping(
        MotoListingScope.PUBLIC,
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
            val moto = service.create(body)
            bufferedImages.forEach { file ->
                motoImageService.createFromFile(moto.listingId!!, file)
            }
            ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWithMessage(
                    data = service.findById(
                        moto.listingId!!,
                        includeDocuments = !body.documentIds.isNullOrEmpty(),
                    ),
                    message = "Saved ${bufferedImages.size} images",
                )
            )
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.motolisting.create.count",
                    distributionName = "api.motolisting.create.latency",
                )
            )
        }
    }

    private fun parseListingRequest(listingJson: String): MotoListingRequest {
        val body = try {
            jsonMapper.readValue(listingJson, MotoListingRequest::class.java)
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

    @Operation(summary = "Détail d'une annonce de moto")
    @GetMapping("${MotoListingScope.PUBLIC}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findById(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @RequestParam(defaultValue = "false") includeDocuments: Boolean,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ResponseEntity.ok(service.findById(id, includeDocuments))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.motolisting.findbyid.count",
                    distributionName = "api.motolisting.findbyid.latency",
                )
            )
        }
    }

    @Operation(summary = "Annonces d'un utilisateur")
    @GetMapping("${MotoListingScope.PROTECTED}/user/{userId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByUserId(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable userId: Long,
        @RequestParam(defaultValue = "false") includeDocuments: Boolean,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByUserId(userId, includeDocuments))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.motolisting.findbyuserid.count",
                    distributionName = "api.motolisting.findbyuserid.latency",
                )
            )
        }
    }

    @Operation(summary = "Annonces par type (vente, location, échange)")
    @GetMapping("${MotoListingScope.PUBLIC}/type/{listingType}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByListingType(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable listingType: ListingType,
        @RequestParam(defaultValue = "false") includeDocuments: Boolean,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByListingType(listingType, includeDocuments))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.motolisting.findbylistingtype.count",
                    distributionName = "api.motolisting.findbylistingtype.latency",
                )
            )
        }
    }

    @Operation(summary = "Annonces par modèle de moto")
    @GetMapping("${MotoListingScope.PUBLIC}/model/{motoModelId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByMotoModelId(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable motoModelId: Long,
        @RequestParam(defaultValue = "false") includeDocuments: Boolean,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByMotoModelId(motoModelId, includeDocuments))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.motolisting.findbymotomodelid.count",
                    distributionName = "api.motolisting.findbymotomodelid.latency",
                )
            )
        }
    }

    @Operation(summary = "Annonces par motorisation et état")
    @GetMapping("${MotoListingScope.PUBLIC}/filter", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByElectricAndCondition(
        request: HttpServletRequest,
        @PathVariable version: String,
        @RequestParam isElectric: Boolean,
        @RequestParam condition: ItemCondition,
        @RequestParam(defaultValue = "false") includeDocuments: Boolean,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByElectricAndCondition(isElectric, condition, includeDocuments))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.motolisting.findbyelectricandcondition.count",
                    distributionName = "api.motolisting.findbyelectricandcondition.latency",
                )
            )
        }
    }

    @Operation(summary = "Mettre à jour le statut d'une annonce")
    @PatchMapping("${MotoListingScope.PROTECTED}/{id}/status", produces = [MediaType.APPLICATION_JSON_VALUE])
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
                    countName = "api.motolisting.updatestatus.count",
                    distributionName = "api.motolisting.updatestatus.latency",
                )
            )
        }
    }
}
