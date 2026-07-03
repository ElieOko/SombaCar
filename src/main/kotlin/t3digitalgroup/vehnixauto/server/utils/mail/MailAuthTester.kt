package t3digitalgroup.vehnixauto.server.utils.mail

import jakarta.annotation.*
import jakarta.mail.*
import org.springframework.mail.javamail.*
import org.springframework.stereotype.*

@Component
class MailAuthTester(
    private val javaMailSender: JavaMailSender
) {

    @PostConstruct
    fun testAuthentication() {
        try {
            val sender = javaMailSender as JavaMailSenderImpl
            val session = sender.session

            println("Testing mail authentication...")
            println("Host: ${sender.host}")
            println("Port: ${sender.port}")
            println("Username: ${sender.username}")
            println("Password: ${"*".repeat(sender.password?.length ?: 0)}")

            val transport = session.getTransport("smtp")
            transport.connect(sender.host, sender.port, sender.username, sender.password)
            println("✅ Authentication successful!")
            transport.close()

        } catch (e: AuthenticationFailedException) {
            println("❌ Authentication failed: ${e.message}")
            println("Please check:")
            println("1. Your email and password are correct")
            println("2. For Gmail, use an App Password if 2FA is enabled")
            println("3. Ensure 'Allow less secure apps' is enabled if not using 2FA")
        } catch (e: Exception) {
            println("❌ Connection failed: ${e.message}")
        }
    }
}