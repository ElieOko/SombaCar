package t3digitalgroup.vehnixauto.server.utils.mail

import org.slf4j.*
import org.springframework.mail.*
import org.springframework.mail.javamail.*
import org.springframework.stereotype.*

@Service
class SenderMailAuth {
    lateinit var mailSender: MailSender
    lateinit var templateMessage: SimpleMailMessage
    private val log = LoggerFactory.getLogger(this::class.java)
    companion object {
        private const val PRIMARY_COLOR = "#4F46E5"
        private const val SECONDARY_COLOR = "#10B981"
        private const val DANGER_COLOR = "#EF4444"
        private const val TEXT_COLOR = "#374151"
        private const val BG_COLOR = "#F9FAFB"
    }
    suspend fun sendMail(to : String = "elieoko100@gmail.com",otp : String, time:String) : String{
        try {
//            val msg = SimpleMailMessage(this.templateMessage)
            log.info("$to ************")
            val message = (mailSender as JavaMailSender).createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")
            helper.setFrom("contact@casanayo.com","LawApp50")
            helper.setTo(to)
            helper.setSubject("Code de Validation")
            helper.setText("",buildHtmlEmail(otp)) // true = HTML
//           mailSender.send(msg)
            (mailSender as JavaMailSender).send(message)
            return "Mail Sent Successfully"
        } catch (ex: MailException) {
            System.err.println(ex.message)
            return "Error while sending mail"
        }
    }

    private fun buildHtmlEmail(otp: String): String {
        return """
             <!doctype html>
             <html>
               <head>
                 <meta charset="UTF-8" />
                 <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                 <title>Code de vérification</title>
               </head>
               <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif">
                 <div style="max-width: 600px; margin: 0 auto; background-color: #f4f4f4">
                   <!-- Header -->
                   <div style="background-color: #4f46e5; padding: 20px; text-align: center">
                     <h1 style="color: white; margin: 0">LawApp50</h1>
                     <p style="color: #c7d2fe; margin: 5px 0 0">Vérification de sécurité</p>
                   </div>

                   <!-- Content -->
                   <div style="background-color: white; padding: 40px 30px; border-radius: 8px; margin: 20px">
                     <h2 style="color: #1f2937; margin-top: 0">Bonjour 👋</h2>
                     <p style="color: #4b5563; line-height: 1.6">
                     Merci d'avoir créé votre compte sur <strong>LawApp50</strong> ! 🎉<br>Pour finaliser votre inscription et sécuriser votre accès, veuillez utiliser le code de vérification ci-dessous :
                     </p>
                    
                     <!-- Code OTP -->
                     <div
                       style="
                         background-color: #f3f4f6;
                         padding: 20px;
                         text-align: center;
                         margin: 30px 0;
                         border-radius: 8px;
                       "
                     >
                       <span style="font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #4f46e5"
                         >$otp</span
                       >
                     </div>
                     <div
                       style="
                         background-color: #fef3c7;
                         border-left: 4px solid #f59e0b;
                         padding: 15px;
                         margin: 20px 0;
                       "
                     >
                       <p style="margin: 0; color: #92400e; font-size: 14px">
                         ⚠️ <strong>Important :</strong> Ne partagez ce code avec personne. Notre équipe ne vous
                         demandera jamais ce code.
                       </p>
                     </div>
                     <hr style="border: none; border-top: 1px solid #e5e7eb; margin: 30px 0" />
                   </div>
                   <!-- Footer -->
                   <div style="background-color: #f9fafb; padding: 20px; text-align: center">
                     <p style="color: #6b7280; font-size: 12px; margin: 0">
                       © 2026 LawApp50. Tous droits réservés.
                     </p>
                     <p style="color: #9ca3af; font-size: 12px; margin: 10px 0 0">
                       Cet email a été envoyé automatiquement pour la validation de votre compte, merci de ne pas y répondre.
                     </p>
                   </div>
                 </div>
               </body>
             </html>
        """.trimIndent()
    }
}