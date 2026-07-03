package t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities

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
    @JsonIgnore
    @Column("password")
    var password: String? = "",
    @Column("email")
    var email: String? = null,
    @Column("username")
    var username: String? = null,
    @Column("first_name")
    var firstName: String,
    @Column("last_name")
    var lastName: String,
    @Column("city")
    var city: String,
    @Column("full_name")
    var fullName: String = "$firstName $lastName",
    @Column("from_service")
    var fromService : String? = null,
    @Column("is_premium")
    var isPremium: Boolean = false,
    @Column("is_certified")
    var isCertified: Boolean = false,
    @Column("is_lock")
    var isLock: Boolean = false,
    @Column("is_valid")
    var isValid: Boolean = false,
    @Column("phone")
    var phone: String?=null,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
