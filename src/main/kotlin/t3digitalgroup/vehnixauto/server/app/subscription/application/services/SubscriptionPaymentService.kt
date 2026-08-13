package t3digitalgroup.vehnixauto.server.app.subscription.application.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.notification.domain.models.TagType
import t3digitalgroup.vehnixauto.server.app.notification.infrastructure.entities.NotificationEntity
import t3digitalgroup.vehnixauto.server.app.notification.infrastructure.repositories.NotificationRepository
import t3digitalgroup.vehnixauto.server.app.payment.application.services.DeviseService
import t3digitalgroup.vehnixauto.server.app.payment.application.services.FlexPaieService
import t3digitalgroup.vehnixauto.server.app.payment.application.services.PaymentService
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.DeviseType
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.Paiement
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.StatusPayment
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.Transaction
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TransactionCard
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.TypePayment
import t3digitalgroup.vehnixauto.server.app.subscription.domain.models.request.SubscriptionCardPaymentRequest
import t3digitalgroup.vehnixauto.server.app.subscription.domain.models.request.SubscriptionPaymentRequest
import t3digitalgroup.vehnixauto.server.utils.Mode
import t3digitalgroup.vehnixauto.server.utils.PaymentMessages
import t3digitalgroup.vehnixauto.server.utils.PaymentPurpose
import t3digitalgroup.vehnixauto.server.utils.generateTransactionReference
import t3digitalgroup.vehnixauto.server.utils.scheduler.PaymentScheduler
import kotlin.random.Random

@Service
@Profile(Mode.DEV)
class SubscriptionPaymentService(
    private val flexPaieService: FlexPaieService,
    private val paymentService: PaymentService,
    private val deviseService: DeviseService,
    private val planService: SubscriptionPlanService,
    private val userSubscriptionService: UserSubscriptionService,
    private val notificationRepository: NotificationRepository,
    private val paymentScheduler: PaymentScheduler,
    @Value("\${app.payment.callback-base-url:https://driver.vehnixauto.com/api/v1/public/payments}") private val callbackBaseUrl: String,
) {
    suspend fun payMobileMoney(userId: Long, request: SubscriptionPaymentRequest) =
        initiatePayment(
            userId = userId,
            planId = request.planId,
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
                    callbackUrl = "$callbackBaseUrl/subscription/mobile/callback",
                )
            ).code
        }

    suspend fun payCard(userId: Long, request: SubscriptionCardPaymentRequest) =
        initiatePayment(
            userId = userId,
            planId = request.planId,
            deviseId = request.deviseId,
            typePayment = TypePayment.CARD,
            timeoutMinutes = 15L,
        ) { amount, currency, reference ->
            flexPaieService.paymentCard(
                TransactionCard(
                    reference = reference,
                    amount = amount,
                    currency = currency,
                    description = "Abonnement premium VehnixAuto",
                    callback_url = "$callbackBaseUrl/subscription/card/callback",
                )
            ).code
        }

    suspend fun handleCallback(reference: String, code: String) {
        val payment = paymentService.update(reference, code)
        if (code == "0") {
            val planId = payment.subscriptionPlanId
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Plan d'abonnement manquant.")
            userSubscriptionService.activateFromPayment(payment.userId, planId, reference)
            notifyUser(
                userId = payment.userId,
                title = "Abonnement activé",
                message = "Votre compte premium est maintenant actif.",
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

    suspend fun handlePaymentTimeout(reference: String) {
        paymentService.cancelPendingByReference(reference)
    }

    private suspend fun initiatePayment(
        userId: Long,
        planId: Long,
        deviseId: Long,
        typePayment: TypePayment,
        timeoutMinutes: Long,
        executePayment: suspend (amount: String, currency: String, reference: String) -> String?,
    ): Any {
        val plan = planService.requireActivePlan(planId)
        val (amount, currency) = resolveAmount(plan.price, plan.devise, deviseId)
        val reference = generateTransactionReference()
        val code = executePayment(amount, currency, reference)
        if (code == "0") {
            paymentService.create(
                Paiement(
                    userId = userId,
                    reference = reference,
                    amount = amount,
                    devise = currency,
                    description = "Abonnement: ${plan.name}",
                    typePayment = typePayment.name,
                    status = StatusPayment.PENDING.name,
                    purchaseType = PaymentPurpose.SUBSCRIPTION.name,
                    subscriptionPlanId = planId,
                    orderNumber = reference,
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

    private suspend fun resolveAmount(price: String, planDevise: String, deviseId: Long): Pair<String, String> {
        val devise = deviseService.getById(deviseId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Devise introuvable.")
        return when (devise.code.uppercase()) {
            DeviseType.CDF.name -> {
                val rate = devise.tauxLocal
                    ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Taux CDF manquant.")
                val baseAmount = price.toDoubleOrNull()
                    ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Prix du plan invalide.")
                val converted = if (planDevise.uppercase() == DeviseType.USD.name) {
                    baseAmount * rate
                } else {
                    baseAmount
                }
                converted.toLong().toString() to DeviseType.CDF.name
            }
            DeviseType.USD.name -> {
                if (planDevise.uppercase() != DeviseType.USD.name) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce plan n'est pas disponible en USD.")
                }
                price to DeviseType.USD.name
            }
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Devise non supportée.")
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
