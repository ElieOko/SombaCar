package initializer.backend.spring.app.user.infrastructure.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "users")
class UserEntity(
    @Id
    @Column("id")
    val userId: Long? = null,
    @Column("city")
    var city: String? = null,
    @JsonIgnore
    @Column("password")
    var password: String? = "",
    @Column("email")
    var email: String? = null,
    @Column("username")
    var username: String? = null,
    @Column("is_premium")
    var isPremium: Boolean = false,
    @Column("is_certified")
    var isCertified: Boolean = false,
    @Column("is_lock")
    var isLock: Boolean = false,
    @Column("phone")
    var phone: String?=null,
    @Column("country")
    var country: String? = "Democratic Republic of the Congo",
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
