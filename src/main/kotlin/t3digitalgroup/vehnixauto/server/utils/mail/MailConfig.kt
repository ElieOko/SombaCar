package t3digitalgroup.vehnixauto.server.utils.mail

import org.springframework.beans.factory.annotation.*
import org.springframework.context.annotation.*
import org.springframework.mail.*
import org.springframework.mail.javamail.*
import java.util.*

@Configuration
class MailConfig(
    @Value("\${spring.mail.host}")
    private val host: String,
    @Value("\${spring.mail.port}")
    private val port: Int,
    @Value("\${spring.mail.username}")
    private val usernameMail: String,
    @Value("\${spring.mail.password}")
    private val passwordMail: String
) {
    @Bean
    fun javaMailSender(): JavaMailSender {
        return JavaMailSenderImpl().apply {
            this.host = host
            this.port = port
            this.username = usernameMail
            this.password = passwordMail
            this.javaMailProperties = Properties().apply {
                setProperty("mail.smtp.auth", "true")
                if (port == 465) {
                    setProperty("mail.smtp.ssl.enable", "true")
                } else {
                    setProperty("mail.smtp.starttls.enable", "true")
                }
            }
        }
    }

    @Bean
    fun mailSender(): JavaMailSender {
        return JavaMailSenderImpl().apply {
            host = "smtp.hostinger.com"
        }
    }
    @Bean
    fun templateMessage(): SimpleMailMessage {
        return SimpleMailMessage().apply {
            from = "contact@casanayo.com"
            subject = "Code de vérification"
        }
    }

    @Bean
    fun senderMailAuth(javaMailSender: JavaMailSender, simpleTemplateMessage: SimpleMailMessage) = SenderMailAuth().apply {
        mailSender = javaMailSender
        templateMessage = simpleTemplateMessage
    }
}