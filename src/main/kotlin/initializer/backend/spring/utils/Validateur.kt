package initializer.backend.spring.utils
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

fun normalizeAndValidatePhoneNumberUniversal(
    phone: String?,
    defaultRegion: String = "ZZ" // ZZ = inconnu → libphonenumber tente de deviner
): String? {
    if (phone.isNullOrBlank()) return null

    // 1️⃣ Nettoyage
    var cleaned = phone.replace("[^0-9+]+".toRegex(), "")

    // 2️⃣ Transforme 00 → +
    if (cleaned.startsWith("00")) {
        cleaned = "+" + cleaned.drop(2)
    }

    val util = PhoneNumberUtil.getInstance()

    return try {
        val parsed = util.parse(cleaned, defaultRegion)

        // 3️⃣ Valide format complet selon pays / longueur / type
        if (!util.isValidNumber(parsed)) return null

        // 4️⃣ Retourne propre en E.164
        util.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164)

    } catch (e: NumberParseException) {
        null
    }
}

fun isEmailValid(email: String?): Boolean {
    if (email.isNullOrBlank()) return false

    val emailRegex = Regex(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )

    return emailRegex.matches(email)
}

fun toPascalCase(input: String): String {
    return input.split(" ", "-", "_") // sépare selon espaces ou tirets
        .filter { it.isNotEmpty() }   // enlève les mots vides
        .joinToString("") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
}