package t3digitalgroup.vehnixauto.server.app.payment.application.services

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.Paiement
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.PaymentDTO
import t3digitalgroup.vehnixauto.server.app.payment.domain.models.StatusPayment
import t3digitalgroup.vehnixauto.server.app.payment.infrastructure.mapper.toDomain
import t3digitalgroup.vehnixauto.server.app.payment.infrastructure.mapper.toEntity
import t3digitalgroup.vehnixauto.server.app.payment.infrastructure.repositories.PaiementRepository
import t3digitalgroup.vehnixauto.server.app.user.application.services.UserService
import t3digitalgroup.vehnixauto.server.utils.Mode
import java.time.LocalDate

@Service
@Profile(Mode.DEV)
class PaymentService(
    private val repository: PaiementRepository,
    private val user: UserService,
) {
    suspend fun create(model: Paiement): Paiement = coroutineScope {
        repository.save(model.toEntity()).toDomain()
    }

    private suspend fun logPayment(userId: Long) = coroutineScope {
        repository.findByUser(userId)
    }

    suspend fun update(reference: String, code: String): Paiement = coroutineScope {
        val data = referencePayment(reference)
        data.status = when (code) {
            "0" -> StatusPayment.SUCCESS.name
            else -> StatusPayment.CANCELLED.name
        }
        data.dateUpdated = LocalDate.now()
        repository.save(data).toDomain()
    }

    private suspend fun referencePayment(reference: String) = coroutineScope {
        val state = repository.findByReference(reference).toList().filterNotNull()
        if (state.isNotEmpty()) state[0] else throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette reference n'existe pas.")
    }

    suspend fun showDetail(id: Long): Paiement = coroutineScope {
        val data = repository.findById(id) ?: throw ResponseStatusException(
            HttpStatusCode.valueOf(404),
            "ID Is Not Found.",
        )
        data.toDomain()
    }

    suspend fun showAll(): List<PaymentDTO> = coroutineScope {
        val items = mutableListOf<PaymentDTO>()
        repository.findAll().collect { items.add(owner(it.userId)) }
        items
    }

    suspend fun owner(userId: Long): PaymentDTO = coroutineScope {
        val items = mutableListOf<Paiement>()
        val userDto = user.findIdUser(userId)
        logPayment(userId).collect { entity -> entity?.let { items.add(it.toDomain()) } }
        PaymentDTO(payment = items, user = userDto)
    }

    suspend fun cancelPendingByReference(reference: String) {
        val state = repository.findByReference(reference).toList().filterNotNull()
        if (state.isEmpty()) return
        val data = state.first()
        if (data.status == StatusPayment.PENDING.name) {
            data.status = StatusPayment.CANCELLED.name
            data.dateUpdated = LocalDate.now()
            repository.save(data)
        }
    }
}
