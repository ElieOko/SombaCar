package t3digitalgroup.vehnixauto.server.app.payment.application.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.car.application.services.CarListingService
import t3digitalgroup.vehnixauto.server.app.moto.application.services.MotoListingService
import t3digitalgroup.vehnixauto.server.app.notification.domain.models.TagType
import t3digitalgroup.vehnixauto.server.app.notification.infrastructure.entities.NotificationEntity
import t3digitalgroup.vehnixauto.server.app.notification.infrastructure.repositories.NotificationRepository
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.DeviseType
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.Paiement
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.StatusPayment
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.Transaction
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TransactionCard
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TransactionCardRequest
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TransactionRequest
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TypePayment
import t3digitalgroup.vehnixauto.server.app.sale.application.services.SaleOfferService
import t3digitalgroup.vehnixauto.server.app.tools.application.services.PartListingService
import t3digitalgroup.vehnixauto.server.utils.ListingStatus
import t3digitalgroup.vehnixauto.server.utils.Mode
import t3digitalgroup.vehnixauto.server.utils.OfferType
import t3digitalgroup.vehnixauto.server.utils.PaymentMessages
import t3digitalgroup.vehnixauto.server.utils.generateTransactionReference
import t3digitalgroup.vehnixauto.server.utils.scheduler.PaymentScheduler
import kotlin.random.Random

@Service
@Profile(Mode.DEV)
class PurchasePaymentService(
    private val flexPaieService: FlexPaieService,
    private val paymentService: PaymentService,
    private val deviseService: DeviseService,
    private val saleOfferService: SaleOfferService,
    private val carListingService: CarListingService,
    private val motoListingService: MotoListingService,
    private val partListingService: PartListingService,
    private val notificationRepository: NotificationRepository,
    private val paymentScheduler: PaymentScheduler,
    @Value("\${app.payment.callback-base-url:https://api.vehnixauto.com/api/v1/public/payments}") private val callbackBaseUrl: String,
) {
    suspend fun payMobileMoney(userId: Long, request: TransactionRequest) =
        initiatePayment(
            userId = userId,
            offerId = request.offerId,
            deviseId = request.deviseId,
            typePayment = TypePayment.MOBILE_MONEY,
            timeoutMinutes = 2L,
        ) { amount, currency, reference ->
            flexPaieService.paymentMobileMoney(
                Transaction(
                    phone = request.phone,
                    reference = reference,
                    amount = amount,
                    currency = currency,
                    callbackUrl = "$callbackBaseUrl/mobile/callback",
                )
            ).code
        }

    suspend fun payCard(userId: Long, request: TransactionCardRequest) =
        initiatePayment(
            userId = userId,
            offerId = request.offerId,
            deviseId = request.deviseId,
            typePayment = TypePayment.CARD,
            timeoutMinutes = 15L,
        ) { amount, currency, reference ->
            flexPaieService.paymentCard(
                TransactionCard(
                    reference = reference,
                    amount = amount,
                    currency = currency,
                    callback_url = "$callbackBaseUrl/card/callback",
                )
            ).code
        }

    suspend fun handleCallback(reference: String, code: String) {
        val payment = paymentService.update(reference, code)
        if (code == "0") {
            finalizeSuccessfulPurchase(payment)
            notifyUser(
                userId = payment.userId,
                title = "Paiement réussi",
                message = PaymentMessages.PAYMENT_SUCCESS,
                tag = TagType.FINANCES,
            )
        } else {
            notifyUser(
                userId = payment.userId,
                title = "Paiement annulé",
                message = PaymentMessages.PAYMENT_CANCEL,
                tag = TagType.FINANCES,
            )
        }
    }

    private suspend fun initiatePayment(
        userId: Long,
        offerId: Long,
        deviseId: Long,
        typePayment: TypePayment,
        timeoutMinutes: Long,
        executePayment: suspend (amount: String, currency: String, reference: String) -> String?,
    ): Any {
        val offer = saleOfferService.requireActiveOffer(offerId)
        val (amount, currency) = resolveAmount(offer.price, offer.devise, deviseId)
        val reference = generateTransactionReference()
        val code = executePayment(amount, currency, reference)
        if (code == "0") {
            paymentService.create(
                Paiement(
                    userId = userId,
                    reference = reference,
                    amount = amount,
                    devise = currency,
                    description = "Achat: ${offer.title}",
                    typePayment = typePayment.name,
                    status = StatusPayment.PENDING.name,
                    offerId = offer.saleOfferId,
                    purchaseType = offer.offerType,
                )
            )
            paymentScheduler.scheduleOneShot(
                taskId = Random.nextInt(1, 1_000_000_000).toLong(),
                reference = reference,
                minute = timeoutMinutes,
            )
        }
        return mapOf("code" to code, "reference" to reference, "amount" to amount, "currency" to currency)
    }

    private suspend fun resolveAmount(price: String, offerDevise: String, deviseId: Long): Pair<String, String> {
        val devise = deviseService.getById(deviseId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Devise introuvable.")
        return when (devise.code.uppercase()) {
            DeviseType.CDF.name -> {
                val rate = devise.tauxLocal ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Taux CDF manquant.")
                val baseAmount = price.toDoubleOrNull()
                    ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Prix de l'offre invalide.")
                val converted = if (offerDevise.uppercase() == DeviseType.USD.name) {
                    baseAmount * rate
                } else {
                    baseAmount
                }
                converted.toLong().toString() to DeviseType.CDF.name
            }
            DeviseType.USD.name -> {
                if (offerDevise.uppercase() != DeviseType.USD.name) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette offre n'est pas disponible en USD.")
                }
                price to DeviseType.USD.name
            }
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Devise non supportée.")
        }
    }

    private suspend fun finalizeSuccessfulPurchase(payment: Paiement) {
        val offerId = payment.offerId ?: return
        val offer = saleOfferService.markAsSold(offerId)
        val listingId = offer.linkedListingId ?: return
        when (OfferType.valueOf(offer.offerType)) {
            OfferType.CAR -> carListingService.updateStatus(listingId, ListingStatus.SOLD)
            OfferType.MOTO -> motoListingService.updateStatus(listingId, ListingStatus.SOLD)
            OfferType.PART -> partListingService.updateStatus(listingId, ListingStatus.SOLD)
        }
    }

    private suspend fun notifyUser(userId: Long, title: String, message: String, tag: TagType) {
        notificationRepository.save(
            NotificationEntity(
                userId = userId,
                title = title,
                message = message,
                tag = tag.name,
            )
        )
    }
}
