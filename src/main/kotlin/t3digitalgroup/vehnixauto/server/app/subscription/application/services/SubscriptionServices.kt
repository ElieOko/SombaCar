package t3digitalgroup.vehnixauto.server.app.subscription.application.services

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.subscription.domain.models.SubscriptionPlan
import t3digitalgroup.vehnixauto.server.app.subscription.domain.models.SubscriptionStatusResponse
import t3digitalgroup.vehnixauto.server.app.subscription.domain.models.UserSubscription
import t3digitalgroup.vehnixauto.server.app.subscription.domain.models.request.SubscriptionPlanRequest
import t3digitalgroup.vehnixauto.server.app.subscription.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.subscription.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.subscription.infrastructure.repositories.SubscriptionPlanRepository
import t3digitalgroup.vehnixauto.server.app.subscription.infrastructure.repositories.UserSubscriptionRepository
import t3digitalgroup.vehnixauto.server.app.user.infrastructure.repositories.UserRepository
import t3digitalgroup.vehnixauto.server.security.PremiumAuthorization
import t3digitalgroup.vehnixauto.server.utils.Mode
import t3digitalgroup.vehnixauto.server.utils.SubscriptionStatus
import java.time.LocalDateTime

@Service
@Profile(Mode.DEV)
class SubscriptionPlanService(
    private val repository: SubscriptionPlanRepository,
) {
    suspend fun findAllActive(): List<SubscriptionPlan> =
        repository.findAllActive().map { it.toDomain() }.toList()

    suspend fun findById(id: Long): SubscriptionPlan =
        repository.findById(id)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Plan d'abonnement introuvable.")

    suspend fun requireActivePlan(id: Long): SubscriptionPlan {
        val plan = findById(id)
        if (!plan.isActive) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce plan n'est plus disponible.")
        }
        return plan
    }

    suspend fun create(request: SubscriptionPlanRequest): SubscriptionPlan =
        repository.save(
            SubscriptionPlan(
                name = request.name,
                description = request.description,
                price = request.price,
                devise = request.devise,
                durationDays = request.durationDays,
            ).toEntity()
        ).toDomain()
}

@Service
@Profile(Mode.DEV)
class UserSubscriptionService(
    private val subscriptionRepository: UserSubscriptionRepository,
    private val planRepository: SubscriptionPlanRepository,
    private val userRepository: UserRepository,
    private val premiumAuthorization: PremiumAuthorization,
) {
    suspend fun getStatus(userId: Long): SubscriptionStatusResponse {
        val user = userRepository.findById(userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable.")
        val active = subscriptionRepository.findActiveByUserId(userId)?.toDomain()
        return SubscriptionStatusResponse(
            isPremium = premiumAuthorization.isPremium(userId),
            premiumExpiresAt = user.premiumExpiresAt,
            activeSubscription = active,
        )
    }

    suspend fun findHistory(userId: Long): List<UserSubscription> =
        subscriptionRepository.findAllByUserId(userId).map { it.toDomain() }.toList()

    suspend fun activateFromPayment(userId: Long, planId: Long, paymentReference: String) {
        val plan = planRepository.findById(planId)?.toDomain()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Plan introuvable.")
        val user = userRepository.findById(userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable.")

        val now = LocalDateTime.now()
        val baseDate = user.premiumExpiresAt?.takeIf { it.isAfter(now) } ?: now
        val expiresAt = baseDate.plusDays(plan.durationDays.toLong())

        subscriptionRepository.save(
            UserSubscription(
                userId = userId,
                planId = planId,
                paymentReference = paymentReference,
                status = SubscriptionStatus.ACTIVE.name,
                startsAt = now,
                expiresAt = expiresAt,
            ).toEntity()
        )

        user.isPremium = true
        user.premiumExpiresAt = expiresAt
        userRepository.save(user)
    }
}
