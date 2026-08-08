package t3digitalgroup.vehnixauto.server.utils.scheduler

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import t3digitalgroup.vehnixauto.server.app.payment.application.services.PaymentService
import t3digitalgroup.vehnixauto.server.utils.Mode

@Service
@Profile(Mode.DEV)
class PaymentTaskService(
    private val paymentService: PaymentService,
) {
    suspend fun cancelPendingPayment(reference: String) {
        paymentService.cancelPendingByReference(reference)
    }
}
