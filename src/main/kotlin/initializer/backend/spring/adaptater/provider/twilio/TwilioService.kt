package initializer.backend.spring.adaptater.provider.twilio

import com.twilio.rest.verify.v2.service.Verification
import com.twilio.rest.verify.v2.service.VerificationCheck
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class TwilioService(
    @Autowired private val twilioConfig: TwilioConfig
)  {
    private val log = LoggerFactory.getLogger(this::class.java)
    fun generateVerifyOTP(
        contact: String = "+243827824163",
        channel : String = "sms"
    ): String? {
      val verification = Verification
          .creator("VA7016e2d1a784a728342ea285a4eb1d63",contact, channel)
          .setLocale("fr")
          .create()
        log.info("Status :${verification.status}")
        log.info("Status :${verification.serviceSid}")
       return verification.status
    }

    fun checkVerify(
        code: String,
        contact: String = "+243827824163"
    ): String? {
        val verificationCheck = VerificationCheck
            .creator("VA7016e2d1a784a728342ea285a4eb1d63")
            .setCode(code)
            .setTo(contact)
            .create()
       return verificationCheck.status
    }
}