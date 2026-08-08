package t3digitalgroup.vehnixauto.server.app.sale.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.sale.domain.models.SaleOffer
import t3digitalgroup.vehnixauto.server.app.sale.domain.models.request.SaleOfferRequest
import t3digitalgroup.vehnixauto.server.app.sale.domain.models.request.SaleOfferUpdateRequest
import t3digitalgroup.vehnixauto.server.app.sale.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.sale.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.sale.infrastructure.repositories.SaleOfferRepository
import t3digitalgroup.vehnixauto.server.utils.Mode
import t3digitalgroup.vehnixauto.server.utils.OfferStatus
import t3digitalgroup.vehnixauto.server.utils.OfferType
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class SaleOfferService(
    private val repository: SaleOfferRepository,
) {
    suspend fun create(request: SaleOfferRequest, createdBy: Long): SaleOffer {
        validateOfferRequest(request)
        return repository.save(
            SaleOffer(
                offerType = request.offerType.name,
                title = request.title.trim(),
                description = request.description?.trim(),
                price = request.price.trim(),
                devise = request.devise.trim().uppercase(),
                linkedListingId = request.linkedListingId,
                createdBy = createdBy,
            ).toEntity()
        ).toDomain()
    }

    suspend fun update(id: Long, request: SaleOfferUpdateRequest): SaleOffer {
        validateOfferUpdateRequest(request)
        val entity = repository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Offre introuvable.")
        entity.offerType = request.offerType.name
        entity.title = request.title.trim()
        entity.description = request.description?.trim()
        entity.price = request.price.trim()
        entity.devise = request.devise.trim().uppercase()
        entity.linkedListingId = request.linkedListingId
        entity.status = request.status
        entity.updatedAt = LocalDateTime.now()
        return repository.save(entity).toDomain()
    }

    suspend fun findById(id: Long): SaleOffer =
        repository.findById(id)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Offre introuvable.")

    suspend fun findAllActive(): List<SaleOffer> =
        repository.findAllActive().map { it.toDomain() }.toList()

    suspend fun findAll(): List<SaleOffer> =
        repository.findAllOrdered().map { it.toDomain() }.toList()

    suspend fun findActiveByType(offerType: OfferType): List<SaleOffer> =
        repository.findActiveByType(offerType.name).map { it.toDomain() }.toList()

    suspend fun markAsSold(id: Long): SaleOffer {
        val entity = repository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Offre introuvable.")
        entity.status = OfferStatus.SOLD.name
        entity.updatedAt = LocalDateTime.now()
        return repository.save(entity).toDomain()
    }

    suspend fun deactivate(id: Long): SaleOffer {
        val entity = repository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Offre introuvable.")
        entity.status = OfferStatus.INACTIVE.name
        entity.updatedAt = LocalDateTime.now()
        return repository.save(entity).toDomain()
    }

    suspend fun requireActiveOffer(id: Long): SaleOffer {
        val offer = findById(id)
        if (offer.status != OfferStatus.ACTIVE.name) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette offre n'est plus disponible.")
        }
        return offer
    }

    private fun validateOfferRequest(request: SaleOfferRequest) {
        if (request.price.toDoubleOrNull() == null || request.price.toDouble() <= 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Prix invalide.")
        }
    }

    private fun validateOfferUpdateRequest(request: SaleOfferUpdateRequest) {
        if (request.price.toDoubleOrNull() == null || request.price.toDouble() <= 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Prix invalide.")
        }
    }
}
