package t3digitalgroup.vehnixauto.server.utils.mail

import org.springframework.beans.factory.annotation.*
import org.springframework.context.annotation.*
import org.springframework.mail.*
import org.springframework.mail.javamail.*
import java.util.*

@Configuration
class MailConfig(
    @Value("\${spring.mail.username}")
    private val usernameMail: String,
    @Value("\${spring.mail.password}")
    private val passwordMail: String
) {
    @Bean
    fun javaMailSender(): JavaMailSender {
        return JavaMailSenderImpl().apply {
            this.host = "smtp.hostinger.com"
            this.port = 587
            this.username = usernameMail
            this.password = passwordMail
            this.javaMailProperties = Properties().apply {
                setProperty("mail.smtp.auth", "true")
                setProperty("mail.smtp.starttls.enable", "true")
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