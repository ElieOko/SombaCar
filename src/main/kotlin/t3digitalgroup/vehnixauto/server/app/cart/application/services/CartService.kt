package t3digitalgroup.vehnixauto.server.app.cart.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.cart.domain.models.CartItem
import t3digitalgroup.vehnixauto.server.app.cart.domain.models.CartSummary
import t3digitalgroup.vehnixauto.server.app.cart.domain.models.request.CartItemRequest
import t3digitalgroup.vehnixauto.server.app.cart.domain.models.request.CartItemUpdateRequest
import t3digitalgroup.vehnixauto.server.app.cart.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.cart.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.cart.infrastructure.repositories.CartItemRepository
import t3digitalgroup.vehnixauto.server.app.payment.application.services.DeviseService
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.DeviseType
import t3digitalgroup.vehnixauto.server.app.tools.application.services.PartListingService
import t3digitalgroup.vehnixauto.server.utils.ListingStatus
import t3digitalgroup.vehnixauto.server.utils.ListingType
import t3digitalgroup.vehnixauto.server.utils.Mode
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class CartService(
    private val repository: CartItemRepository,
    private val partListingService: PartListingService,
    private val deviseService: DeviseService,
) {
    suspend fun addItem(userId: Long, request: CartItemRequest): CartItem {
        val part = partListingService.findById(request.toolsId)
        if (part.status != ListingStatus.ACTIVE.name) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette pièce n'est plus disponible.")
        }
        if (part.listingType != ListingType.SALE.name) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Seules les pièces en vente peuvent être ajoutées au panier.")
        }
        val unitPrice = part.price
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Prix de la pièce manquant.")
        val unitAmount = unitPrice.toDoubleOrNull()
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Prix de la pièce invalide.")
        val totalPrice = (unitAmount * request.quantity).toString()

        val existing = repository.findActiveByUserIdAndToolsId(userId, request.toolsId)
        if (existing != null) {
            existing.quantity = request.quantity
            existing.totalPrice = (unitAmount * request.quantity).toString()
            existing.updatedAt = LocalDateTime.now()
            return repository.save(existing).toDomain()
        }

        return repository.save(
            CartItem(
                userId = userId,
                partType = part.listingType,
                toolsId = request.toolsId,
                quantity = request.quantity,
                unitPrice = unitPrice,
                totalPrice = totalPrice,
                devise = DeviseType.USD.name,
            ).toEntity()
        ).toDomain()
    }

    suspend fun findActiveByUserId(userId: Long): List<CartItem> =
        repository.findActiveByUserId(userId).map { it.toDomain() }.toList()

    suspend fun findInactiveByUserId(userId: Long): List<CartItem> =
        repository.findInactiveByUserId(userId).map { it.toDomain() }.toList()

    suspend fun getSummary(userId: Long, deviseId: Long): CartSummary {
        val items = repository.findCheckoutReadyByUserId(userId).map { it.toDomain() }.toList()
        if (items.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Votre panier est vide.")
        }
        val (totalAmount, currency) = computeTotal(items, deviseId)
        return CartSummary(
            items = items,
            totalAmount = totalAmount,
            currency = currency,
            itemCount = items.sumOf { it.quantity },
        )
    }

    suspend fun updateQuantity(userId: Long, cartItemId: Long, request: CartItemUpdateRequest): CartItem {
        val entity = requireActiveCartItem(userId, cartItemId)
        if (entity.paymentReference != null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet article est en cours de paiement.")
        }
        val unitAmount = entity.unitPrice.toDoubleOrNull()
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Prix unitaire invalide.")
        entity.quantity = request.quantity
        entity.totalPrice = (unitAmount * request.quantity).toString()
        entity.updatedAt = LocalDateTime.now()
        return repository.save(entity).toDomain()
    }

    suspend fun deactivateItem(userId: Long, cartItemId: Long): CartItem {
        val entity = requireActiveCartItem(userId, cartItemId)
        if (entity.paymentReference != null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet article est en cours de paiement.")
        }
        entity.isActive = false
        entity.updatedAt = LocalDateTime.now()
        return repository.save(entity).toDomain()
    }

    suspend fun clearActiveCart(userId: Long): List<CartItem> {
        val items = repository.findCheckoutReadyByUserId(userId).map { it.toDomain() }.toList()
        return items.map { item ->
            val entity = requireActiveCartItem(userId, item.cartItemId!!)
            entity.isActive = false
            entity.updatedAt = LocalDateTime.now()
            repository.save(entity).toDomain()
        }
    }

    suspend fun reserveForPayment(userId: Long, paymentReference: String): List<CartItem> {
        val items = repository.findCheckoutReadyByUserId(userId).map { it.toDomain() }.toList()
        if (items.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Votre panier est vide.")
        }
        return items.map { item ->
            val entity = requireActiveCartItem(userId, item.cartItemId!!)
            entity.paymentReference = paymentReference
            entity.updatedAt = LocalDateTime.now()
            repository.save(entity).toDomain()
        }
    }

    suspend fun computeCheckoutTotal(userId: Long, deviseId: Long): Pair<String, String> {
        val items = repository.findCheckoutReadyByUserId(userId).map { it.toDomain() }.toList()
        if (items.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Votre panier est vide.")
        }
        return computeTotal(items, deviseId)
    }

    suspend fun finalizePurchase(paymentReference: String): List<CartItem> {
        val items = repository.findByPaymentReference(paymentReference).map { it.toDomain() }.toList()
        return items.map { item ->
            val entity = repository.findById(item.cartItemId!!)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article panier introuvable.")
            entity.isActive = false
            entity.updatedAt = LocalDateTime.now()
            repository.save(entity).toDomain()
        }
    }

    suspend fun releaseByPaymentReference(paymentReference: String) {
        val items = repository.findByPaymentReference(paymentReference).map { it.toDomain() }.toList()
        items.forEach { item ->
            val entity = repository.findById(item.cartItemId!!) ?: return@forEach
            entity.paymentReference = null
            entity.updatedAt = LocalDateTime.now()
            repository.save(entity)
        }
    }

    suspend fun findByPaymentReference(paymentReference: String): List<CartItem> =
        repository.findByPaymentReference(paymentReference).map { it.toDomain() }.toList()

    private suspend fun requireActiveCartItem(userId: Long, cartItemId: Long) =
        repository.findById(cartItemId)?.also { item ->
            if (item.userId != userId) {
                throw ResponseStatusException(HttpStatus.FORBIDDEN, "Accès non autorisé.")
            }
            if (!item.isActive) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet article n'est plus actif.")
            }
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article panier introuvable.")

    private suspend fun computeTotal(items: List<CartItem>, deviseId: Long): Pair<String, String> {
        val devise = deviseService.getById(deviseId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Devise introuvable.")
        return when (devise.code.uppercase()) {
            DeviseType.CDF.name -> {
                val rate = devise.tauxLocal
                    ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Taux CDF manquant.")
                val total = items.sumOf { item ->
                    val amount = item.totalPrice.toDoubleOrNull()
                        ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Prix panier invalide.")
                    if (item.devise.uppercase() == DeviseType.USD.name) amount * rate else amount
                }
                total.toLong().toString() to DeviseType.CDF.name
            }
            DeviseType.USD.name -> {
                items.forEach { item ->
                    if (item.devise.uppercase() != DeviseType.USD.name) {
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Le panier contient des articles non disponibles en USD.")
                    }
                }
                val total = items.sumOf { item ->
                    item.totalPrice.toDoubleOrNull()
                        ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Prix panier invalide.")
                }
                total.toString() to DeviseType.USD.name
            }
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Devise non supportée.")
        }
    }
}
