package initializer.backend.spring.app.car.application.services

import initializer.backend.spring.app.car.domain.models.CarListing
import initializer.backend.spring.app.car.domain.models.request.CarListingRequest
import initializer.backend.spring.app.car.infrastructure.mapper.toDomain
import initializer.backend.spring.app.car.infrastructure.mapper.toEntity
import initializer.backend.spring.app.car.infrastructure.repositories.CarListingRepository
import initializer.backend.spring.app.car.infrastructure.repositories.CarModelRepository
import initializer.backend.spring.utils.ItemCondition
import initializer.backend.spring.utils.ListingStatus
import initializer.backend.spring.utils.ListingType
import initializer.backend.spring.utils.Mode
import kotlinx.coroutines.flow.map
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class CarListingService(
    private val listingRepository: CarListingRepository,
    private val carModelRepository: CarModelRepository
) {
    suspend fun create(request: CarListingRequest): CarListing {
        validateListingRequest(request)
        val carModel = carModelRepository.findById(request.carModelId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Modèle de voiture introuvable.")

        val entity = CarListing(
            userId = request.userId,
            carModelId = request.carModelId,
            listingType = request.listingType.name,
            year = request.year,
            isElectric = request.isElectric,
            mileageKm = request.mileageKm,
            condition = request.condition.name,
            plateNumber = request.plateNumber,
            color = request.color,
            seats = request.seats,
            price = request.price,
            rentPricePerDay = request.rentPricePerDay,
            exchangeDescription = request.exchangeDescription,
            description = request.description,
            city = request.city,
            country = request.country
        ).toEntity()

        return listingRepository.save(entity).toDomain(carModel.toDomain())
    }

    suspend fun findById(id: Long): CarListing {
        val listing = listingRepository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        val carModel = carModelRepository.findById(listing.carModelId)?.toDomain()
        return listing.toDomain(carModel)
    }

    suspend fun findByUserId(userId: Long) = listingRepository.findByUserId(userId).map { listing ->
        val carModel = carModelRepository.findById(listing.carModelId)?.toDomain()
        listing.toDomain(carModel)
    }

    suspend fun findByListingType(listingType: ListingType) =
        listingRepository.findActiveByListingType(listingType.name).map { listing ->
            val carModel = carModelRepository.findById(listing.carModelId)?.toDomain()
            listing.toDomain(carModel)
        }

    suspend fun findByCarModelId(carModelId: Long) =
        listingRepository.findActiveByCarModelId(carModelId).map { listing ->
            val carModel = carModelRepository.findById(listing.carModelId)?.toDomain()
            listing.toDomain(carModel)
        }

    suspend fun findByElectricAndCondition(isElectric: Boolean, condition: ItemCondition) =
        listingRepository.findActiveByElectricAndCondition(isElectric, condition.name).map { listing ->
            val carModel = carModelRepository.findById(listing.carModelId)?.toDomain()
            listing.toDomain(carModel)
        }

    suspend fun updateStatus(listingId: Long, status: ListingStatus): CarListing {
        val entity = listingRepository.findById(listingId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce introuvable.")
        entity.status = status.name
        entity.updatedAt = LocalDateTime.now()
        val carModel = carModelRepository.findById(entity.carModelId)?.toDomain()
        return listingRepository.save(entity).toDomain(carModel)
    }

    private fun validateListingRequest(request: CarListingRequest) {
        when (request.listingType) {
            ListingType.SALE -> {
                if (request.price.isNullOrBlank()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Le prix de vente est obligatoire.")
                }
            }
            ListingType.RENT -> {
                if (request.rentPricePerDay.isNullOrBlank()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Le prix de location journalier est obligatoire.")
                }
            }
            ListingType.EXCHANGE -> {
                if (request.exchangeDescription.isNullOrBlank()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Décrivez la pièce ou la voiture souhaitée en échange.")
                }
            }
        }
        if (request.condition == ItemCondition.USED && request.mileageKm <= 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Indiquez le kilométrage pour un véhicule d'occasion.")
        }
    }
}
