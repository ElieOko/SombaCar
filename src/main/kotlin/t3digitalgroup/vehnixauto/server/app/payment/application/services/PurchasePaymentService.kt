package t3digitalgroup.vehnixauto.server.app.payment.application.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.cart.application.services.CartService
import t3digitalgroup.vehnixauto.server.app.notification.domain.models.TagType
import t3digitalgroup.vehnixauto.server.app.notification.infrastructure.entities.NotificationEntity
import t3digitalgroup.vehnixauto.server.app.notification.infrastructure.repositories.NotificationRepository
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.Paiement
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.StatusPayment
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.Transaction
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TransactionCard
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TransactionCardRequest
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TransactionRequest
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TypePayment
import t3digitalgroup.vehnixauto.server.app.tools.application.services.PartListingService
import t3digitalgroup.vehnixauto.server.utils.ListingStatus
import t3digitalgroup.vehnixauto.server.utils.Mode
import t3digitalgroup.vehnixauto.server.utils.PaymentMessages
import t3digitalgroup.vehnixauto.server.utils.PaymentPurpose
import t3digitalgroup.vehnixauto.server.utils.generateTransactionReference
import t3digitalgroup.vehnixauto.server.utils.scheduler.PaymentScheduler
import kotlin.random.Random

@Service
@Profile(Mode.DEV)
class PurchasePaymentService(
    private val flexPaieService: FlexPaieService,
    private val paymentService: PaymentService,
    private val cartService: CartService,
    private val partListingService: PartListingService,
    private val notificationRepository: NotificationRepository,
    private val paymentScheduler: PaymentScheduler,
    @Value("\${app.payment.callback-base-url:https://driver.vehnixauto.com/api/v1/public/payments}") private val callbackBaseUrl: String,
) {
    suspend fun payMobileMoney(userId: Long, request: TransactionRequest) =
        initiatePayment(
            userId = userId,
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
            finalizeSuccessfulPurchase(reference)
            notifyUser(
                userId = payment.userId,
                title = "Paiement réussi",
                message = PaymentMessages.PAYMENT_SUCCESS,
                tag = TagType.FINANCES,
            )
        } else {
            cartService.releaseByPaymentReference(reference)
            notifyUser(
                userId = payment.userId,
                title = "Paiement annulé",
                message = PaymentMessages.PAYMENT_CANCEL,
                tag = TagType.FINANCES,
            )
        }
    }

    suspend fun handlePaymentTimeout(reference: String) {
        paymentService.cancelPendingByReference(reference)
        cartService.releaseByPaymentReference(reference)
    }

    private suspend fun initiatePayment(
        userId: Long,
        deviseId: Long,
        typePayment: TypePayment,
        timeoutMinutes: Long,
        executePayment: suspend (amount: String, currency: String, reference: String) -> String?,
    ): Any {
        val (amount, currency) = cartService.computeCheckoutTotal(userId, deviseId)
        val reference = generateTransactionReference()
        cartService.reserveForPayment(userId, reference)
        val code = executePayment(amount, currency, reference)
        if (code == "0") {
            paymentService.create(
                Paiement(
                    userId = userId,
                    reference = reference,
                    amount = amount,
                    devise = currency,
                    description = "Achat panier",
                    typePayment = typePayment.name,
                    status = StatusPayment.PENDING.name,
                    orderNumber = reference,
                    purchaseType = PaymentPurpose.PURCHASE.name,
                )
            )
            paymentScheduler.scheduleOneShot(
                taskId = Random.nextInt(1, 1_000_000_000).toLong(),
                reference = reference,
                minute = timeoutMinutes,
            )
        } else {
            cartService.releaseByPaymentReference(reference)
        }
        return mapOf("code" to code, "reference" to reference, "amount" to amount, "currency" to currency)
    }

    private suspend fun finalizeSuccessfulPurchase(reference: String) {
        val cartItems = cartService.finalizePurchase(reference)
        cartItems.forEach { item ->
            partListingService.updateStatus(item.toolsId, ListingStatus.SOLD)
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
