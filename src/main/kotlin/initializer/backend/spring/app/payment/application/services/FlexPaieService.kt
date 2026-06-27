package initializer.backend.spring.app.payment.application.services

import initializer.backend.spring.app.payment.domain.models.FlexCheck
import initializer.backend.spring.app.payment.domain.models.ResponseTransaction
import initializer.backend.spring.app.payment.domain.models.ResponseTransactionCard
import initializer.backend.spring.app.payment.domain.models.Transaction
import initializer.backend.spring.app.payment.domain.models.TransactionCard
import initializer.backend.spring.utils.Mode
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono

/*
@Service
@Profile(Mode.DEV)
class FlexPaieService(
    @Value("\${app.api.key}")
    private val apiKeyFlex: String,
    private val builder: WebClient.Builder
) {
    private val clientMobile: WebClient = builder.baseUrl("https://backend.flexpay.cd/api/rest/v1/paymentService").build()
    private val clientCard: WebClient = builder.baseUrl("https://cardpayment.flexpay.cd/v1.1/pay").build()
    private val clientCheck: WebClient = builder.baseUrl("https://apicheck.flexpaie.com/api/rest/v1/check").build()
    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun paymentMobileMoney(transaction: Transaction): ResponseTransaction {
        return clientMobile
            .post()
            .header(HttpHeaders.AUTHORIZATION, apiKeyFlex)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(transaction)
            .retrieve()
            .awaitBody()
    }

    suspend fun checkStateTransaction(orderNumber: String): Mono<FlexCheck> = clientCheck
        .get()
        .uri("/$orderNumber")
        .header(HttpHeaders.AUTHORIZATION, apiKeyFlex)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono(String::class.java).map { msg -> RuntimeException("Client error: $msg") }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            it.bodyToMono(String::class.java).map { msg -> RuntimeException("Problem Server error: $msg") }
        }
        .bodyToMono(FlexCheck::class.java)

    suspend fun paymentCard(transaction: TransactionCard): ResponseTransactionCard {
        transaction.authorization = apiKeyFlex
        return clientCard
            .post()
            .header(HttpHeaders.AUTHORIZATION, apiKeyFlex)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(transaction)
            .retrieve()
            .awaitBody()
    }
}
*/