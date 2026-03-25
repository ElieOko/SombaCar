package emy.backend.barua.security

import emy.backend.barua.utils.Mode
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
@Profile(Mode.DEV)
class HashEncoder {

    private val bcrypt = BCryptPasswordEncoder()

    fun encode(raw: String): String? = bcrypt.encode(raw)
    fun matches(raw: String, hashed: String): Boolean = bcrypt.matches(raw, hashed)
}