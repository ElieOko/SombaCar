package t3digitalgroup.vehnixauto.server.app.report.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.car.application.services.CarListingService
import t3digitalgroup.vehnixauto.server.app.moto.application.services.MotoListingService
import t3digitalgroup.vehnixauto.server.app.report.domain.models.ListingReport
import t3digitalgroup.vehnixauto.server.app.report.domain.models.ListingReportResult
import t3digitalgroup.vehnixauto.server.app.report.domain.models.request.ListingReportRequest
import t3digitalgroup.vehnixauto.server.app.report.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.report.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.report.infrastructure.repositories.ListingReportRepository
import t3digitalgroup.vehnixauto.server.app.tools.application.services.PartListingService
import t3digitalgroup.vehnixauto.server.utils.ListingStatus
import t3digitalgroup.vehnixauto.server.utils.Mode
import t3digitalgroup.vehnixauto.server.utils.OfferType

@Service
@Profile(Mode.DEV)
class ListingReportService(
    private val repository: ListingReportRepository,
    private val carListingService: CarListingService,
    private val partListingService: PartListingService,
    private val motoListingService: MotoListingService,
) {
    suspend fun report(userId: Long, request: ListingReportRequest): ListingReportResult {
        validateListing(request.listingType, request.listingId, userId)

        val listingType = request.listingType.name
        if (repository.existsByReportedByAndListingTypeAndListingId(userId, listingType, request.listingId)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Vous avez déjà signalé cette annonce.")
        }

        val report = repository.save(
            ListingReport(
                listingType = listingType,
                listingId = request.listingId,
                reportedBy = userId,
                reason = request.reason,
            ).toEntity()
        ).toDomain()

        val reportCount = repository.countByListing(listingType, request.listingId).toInt()
        val listingDeactivated = reportCount >= DEACTIVATION_THRESHOLD &&
            deactivateListing(request.listingType, request.listingId)

        return ListingReportResult(
            report = report,
            reportCount = reportCount,
            listingDeactivated = listingDeactivated,
        )
    }

    suspend fun findByListing(listingType: OfferType, listingId: Long): List<ListingReport> =
        repository.findByListing(listingType.name, listingId).map { it.toDomain() }.toList()

    suspend fun findByUser(userId: Long): List<ListingReport> =
        repository.findByReportedBy(userId).map { it.toDomain() }.toList()

    suspend fun countByListing(listingType: OfferType, listingId: Long): Int =
        repository.countByListing(listingType.name, listingId).toInt()

    private suspend fun validateListing(listingType: OfferType, listingId: Long, reporterId: Long) {
        when (listingType) {
            OfferType.CAR -> {
                val listing = carListingService.findById(listingId)
                if (listing.status != ListingStatus.ACTIVE.name) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette annonce n'est plus active.")
                }
                if (listing.userId == reporterId) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Vous ne pouvez pas signaler votre propre annonce.")
                }
            }
            OfferType.PART -> {
                val listing = partListingService.findById(listingId)
                if (listing.status != ListingStatus.ACTIVE.name) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette annonce n'est plus active.")
                }
                if (listing.userId == reporterId) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Vous ne pouvez pas signaler votre propre annonce.")
                }
            }
            OfferType.MOTO -> {
                val listing = motoListingService.findById(listingId)
                if (listing.status != ListingStatus.ACTIVE.name) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette annonce n'est plus active.")
                }
                if (listing.userId == reporterId) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Vous ne pouvez pas signaler votre propre annonce.")
                }
            }
        }
    }

    private suspend fun deactivateListing(listingType: OfferType, listingId: Long): Boolean {
        when (listingType) {
            OfferType.CAR -> carListingService.updateStatus(listingId, ListingStatus.INACTIVE)
            OfferType.PART -> partListingService.updateStatus(listingId, ListingStatus.INACTIVE)
            OfferType.MOTO -> motoListingService.updateStatus(listingId, ListingStatus.INACTIVE)
        }
        return true
    }

    companion object {
        const val DEACTIVATION_THRESHOLD = 5
    }
}
