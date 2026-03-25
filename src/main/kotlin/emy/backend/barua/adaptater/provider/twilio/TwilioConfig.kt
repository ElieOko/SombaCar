package emy.backend.barua.adaptater.provider.twilio

import com.twilio.Twilio
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class TwilioConfig(
    // reading the Twilio ACCOUNT SID from application.properties
    @Value("\${twilio.account-sid}") val accountSid: String,
    // reading the Twilio AUTH TOKEN from application.properties
    @Value("\${twilio.auth-token}") val authToken: String,
) {
    @PostConstruct
    fun twilioInit() {
        // initializing Twilio
        Twilio.init(
            accountSid,
            authToken
        )
    }
}