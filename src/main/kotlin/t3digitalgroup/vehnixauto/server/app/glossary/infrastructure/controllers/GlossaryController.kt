package t3digitalgroup.vehnixauto.server.app.glossary.infrastructure.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Validator
import kotlinx.coroutines.coroutineScope
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartHttpServletRequest
import t3digitalgroup.vehnixauto.server.app.glossary.application.services.GlossaryFileService
import t3digitalgroup.vehnixauto.server.app.glossary.application.services.GlossaryService
import t3digitalgroup.vehnixauto.server.app.glossary.domain.models.request.GlossaryEntryRequest
import t3digitalgroup.vehnixauto.server.app.glossary.domain.models.request.GlossaryEntryUpdateRequest
import t3digitalgroup.vehnixauto.server.route.GlobalRoute
import t3digitalgroup.vehnixauto.server.route.glossary.GlossaryScope
import t3digitalgroup.vehnixauto.server.security.Auth
import t3digitalgroup.vehnixauto.server.security.monitoring.MetricModel
import t3digitalgroup.vehnixauto.server.security.monitoring.SentryService
import t3digitalgroup.vehnixauto.server.utils.ApiResponse
import t3digitalgroup.vehnixauto.server.utils.ApiResponseWithMessage
import t3digitalgroup.vehnixauto.server.utils.bufferMultipartFile
import tools.jackson.databind.json.JsonMapper

@Tag(name = "Glossary", description = "Gestion du glossaire des pièces auto")
@RestController
@RequestMapping("${GlobalRoute.ROOT}/{version}")
@Profile("dev")
class GlossaryController(
    private val service: GlossaryService,
    private val glossaryFileService: GlossaryFileService,
    private val auth: Auth,
    private val sentry: SentryService,
    private val jsonMapper: JsonMapper,
    private val validator: Validator,
) {
    @Operation(summary = "Créer une entrée du glossaire (admin)")
    @PostMapping(
        GlossaryScope.PROTECTED,
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
            requireAdmin()
            val body = parseGlossaryRequest(resolveGlossaryJson(multipartRequest))
            val adminId = auth.user()?.first?.userId
                ?: throw IllegalStateException("Utilisateur introuvable")
            val bufferedFiles = multipartRequest.getFiles("files")
                .filter { !it.isEmpty }
                .map(::bufferMultipartFile)
            val entry = service.create(body, adminId)
            glossaryFileService.createFromFiles(entry.glossaryId!!, bufferedFiles)
            ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWithMessage(
                    data = service.findById(entry.glossaryId),
                    message = "Entrée créée avec ${bufferedFiles.size} fichier(s)",
                )
            )
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.glossary.create.count",
                    distributionName = "api.glossary.create.latency",
                )
            )
        }
    }

    @Operation(summary = "Mettre à jour une entrée du glossaire (admin)")
    @PutMapping(
        "${GlossaryScope.PROTECTED}/{id}",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    suspend fun update(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @RequestBody body: GlossaryEntryUpdateRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            requireAdmin()
            validate(body)
            ResponseEntity.ok(service.update(id, body))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.glossary.update.count",
                    distributionName = "api.glossary.update.latency",
                )
            )
        }
    }

    @Operation(summary = "Supprimer une entrée du glossaire (admin)")
    @DeleteMapping("${GlossaryScope.PROTECTED}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun delete(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            requireAdmin()
            service.delete(id)
            ResponseEntity.ok(mapOf("message" to "Entrée supprimée"))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.glossary.delete.count",
                    distributionName = "api.glossary.delete.latency",
                )
            )
        }
    }

    @Operation(summary = "Ajouter des fichiers à une entrée du glossaire (admin)")
    @PostMapping(
        "${GlossaryScope.PROTECTED}/{id}/files",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    suspend fun addFiles(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        multipartRequest: MultipartHttpServletRequest,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            requireAdmin()
            service.findById(id)
            val bufferedFiles = multipartRequest.getFiles("files")
                .filter { !it.isEmpty }
                .map(::bufferMultipartFile)
            glossaryFileService.createFromFiles(id, bufferedFiles)
            ResponseEntity.ok(
                ApiResponseWithMessage(
                    data = service.findById(id),
                    message = "${bufferedFiles.size} fichier(s) ajouté(s)",
                )
            )
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.glossary.addfiles.count",
                    distributionName = "api.glossary.addfiles.latency",
                )
            )
        }
    }

    @Operation(summary = "Supprimer un fichier d'une entrée du glossaire (admin)")
    @DeleteMapping("${GlossaryScope.PROTECTED}/{id}/files/{fileId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun deleteFile(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable id: Long,
        @PathVariable fileId: Long,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            requireAdmin()
            glossaryFileService.deleteById(id, fileId)
            ResponseEntity.ok(mapOf("message" to "Fichier supprimé"))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.glossary.deletefile.count",
                    distributionName = "api.glossary.deletefile.latency",
                )
            )
        }
    }

    @Operation(summary = "Liste des entrées actives du glossaire")
    @GetMapping(GlossaryScope.PUBLIC, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findAllActive(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findAllActive())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.glossary.findall.count",
                    distributionName = "api.glossary.findall.latency",
                )
            )
        }
    }

    @Operation(summary = "Liste complète du glossaire (admin)")
    @GetMapping("${GlossaryScope.PROTECTED}/all", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findAll(
        request: HttpServletRequest,
        @PathVariable version: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            requireAdmin()
            ApiResponse(service.findAll())
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.glossary.findalladmin.count",
                    distributionName = "api.glossary.findalladmin.latency",
                )
            )
        }
    }

    @Operation(summary = "Détail d'une entrée du glossaire")
    @GetMapping("${GlossaryScope.PUBLIC}/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
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
                    countName = "api.glossary.findbyid.count",
                    distributionName = "api.glossary.findbyid.latency",
                )
            )
        }
    }

    @Operation(summary = "Rechercher dans le glossaire")
    @GetMapping("${GlossaryScope.PUBLIC}/search", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun search(
        request: HttpServletRequest,
        @PathVariable version: String,
        @RequestParam query: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.search(query))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.glossary.search.count",
                    distributionName = "api.glossary.search.latency",
                )
            )
        }
    }

    @Operation(summary = "Entrées du glossaire par catégorie")
    @GetMapping("${GlossaryScope.PUBLIC}/category/{category}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findByCategory(
        request: HttpServletRequest,
        @PathVariable version: String,
        @PathVariable category: String,
    ) = coroutineScope {
        val startNanos = System.nanoTime()
        try {
            ApiResponse(service.findByCategory(category))
        } finally {
            sentry.callToMetric(
                MetricModel(
                    startNanos = startNanos,
                    status = "200",
                    route = "${request.method} /${request.requestURI}",
                    countName = "api.glossary.findbycategory.count",
                    distributionName = "api.glossary.findbycategory.latency",
                )
            )
        }
    }

    private suspend fun requireAdmin() {
        val session = auth.user()
        val isAdmin = session?.second?.find { true } == true
        if (!isAdmin) {
            throw org.springframework.web.server.ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Accès non autorisé",
            )
        }
    }

    private fun parseGlossaryRequest(glossaryJson: String): GlossaryEntryRequest {
        val body = try {
            jsonMapper.readValue(glossaryJson, GlossaryEntryRequest::class.java)
        } catch (e: Exception) {
            throw IllegalArgumentException("JSON glossaire invalide: ${e.message}")
        }
        validate(body)
        return body
    }

    private fun validate(body: Any) {
        val violation = validator.validate(body).firstOrNull()
        if (violation != null) {
            throw IllegalArgumentException(violation.message ?: "Données invalides")
        }
    }

    private fun resolveGlossaryJson(multipartRequest: MultipartHttpServletRequest): String {
        multipartRequest.getParameter("glossary")?.takeIf { it.isNotBlank() }?.let { return it }
        val glossaryPart = multipartRequest.getFile("glossary")
            ?: multipartRequest.getFiles("glossary").firstOrNull()
        if (glossaryPart != null && !glossaryPart.isEmpty) {
            return String(glossaryPart.bytes, Charsets.UTF_8)
        }
        throw IllegalArgumentException("Champ glossary manquant")
    }
}
