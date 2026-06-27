package initializer.backend.spring.app.payment.application.services

import initializer.backend.spring.app.payment.domain.models.Paiement
import initializer.backend.spring.app.payment.domain.models.PaymentDTO
import initializer.backend.spring.app.payment.domain.models.StatusPayment
import initializer.backend.spring.app.payment.infrastructure.mapper.toDomain
import initializer.backend.spring.app.payment.infrastructure.mapper.toEntity
import initializer.backend.spring.app.payment.infrastructure.repositories.PaiementRepository
import initializer.backend.spring.app.user.application.services.UserService
import initializer.backend.spring.utils.Mode
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
@Profile(Mode.DEV)
class PaymentService(
    private val repository: PaiementRepository,
    private val user: UserService
) {
    suspend fun create(model: Paiement) = coroutineScope {
        repository.save(model.toEntity()).toDomain()
    }

    private suspend fun logPayment(userId: Long) = coroutineScope {
        repository.findByUser(userId)
    }

    suspend fun update(reference: String, code: String) = coroutineScope {
        when (code) {
            "0" -> {
                val data = referencePayment(reference)
                data?.status = StatusPayment.SUCCESS.name
                repository.save(data!!)
            }
            else -> {
                val data = referencePayment(reference)
                data?.status = StatusPayment.CANCELLED.name
                repository.save(data!!)
            }
        }
    }

    private suspend fun referencePayment(reference: String) = coroutineScope {
        val state = repository.findByReference(reference).toList().filterNotNull()
        if (state.isNotEmpty()) state[0] else throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette reference n'existe pas !!.")
    }

    suspend fun showDetail(id: Long) = coroutineScope {
        val data = repository.findById(id) ?: throw ResponseStatusException(
            HttpStatusCode.valueOf(404),
            "ID Is Not Found."
        )
        data.toDomain()
    }

    suspend fun showAll() = coroutineScope {
        val items = mutableListOf<PaymentDTO>()
        repository.findAll().collect { items.add(owner(it.userId)) }
        items
    }

    suspend fun owner(userId: Long) = coroutineScope {
        val items = mutableListOf<Paiement>()
        val userDto = user.findIdUser(userId)
        logPayment(userId).collect { entity -> entity?.let { items.add(it.toDomain()) } }
        PaymentDTO(payment = items, user = userDto)
    }
}
