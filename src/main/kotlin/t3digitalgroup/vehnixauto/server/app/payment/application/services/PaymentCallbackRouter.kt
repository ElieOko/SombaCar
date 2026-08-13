package t3digitalgroup.vehnixauto.server.app.payment.application.services

import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.payment.infrastructure.repositories.PaiementRepository
import t3digitalgroup.vehnixauto.server.app.subscription.application.services.SubscriptionPaymentService
import t3digitalgroup.vehnixauto.server.utils.Mode
import t3digitalgroup.vehnixauto.server.utils.PaymentPurpose

@Service
@Profile(Mode.DEV)
class PaymentCallbackRouter(
    private val repository: PaiementRepository,
    private val purchasePaymentService: PurchasePaymentService,
    private val subscriptionPaymentService: SubscriptionPaymentService,
) {
    suspend fun handleCallback(reference: String, code: String) {
        when (resolvePurpose(reference)) {
            PaymentPurpose.SUBSCRIPTION -> subscriptionPaymentService.handleCallback(reference, code)
            PaymentPurpose.PURCHASE -> purchasePaymentService.handleCallback(reference, code)
        }
    }

    suspend fun handlePaymentTimeout(reference: String) {
        when (resolvePurpose(reference)) {
            PaymentPurpose.SUBSCRIPTION -> subscriptionPaymentService.handlePaymentTimeout(reference)
            PaymentPurpose.PURCHASE -> purchasePaymentService.handlePaymentTimeout(reference)
        }
    }

    private suspend fun resolvePurpose(reference: String): PaymentPurpose {
        val payment = repository.findByReference(reference).toList().filterNotNull().firstOrNull()
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette reference n'existe pas.")
        return when (payment.purchaseType) {
            PaymentPurpose.SUBSCRIPTION.name -> PaymentPurpose.SUBSCRIPTION
            else -> PaymentPurpose.PURCHASE
        }
    }
}
