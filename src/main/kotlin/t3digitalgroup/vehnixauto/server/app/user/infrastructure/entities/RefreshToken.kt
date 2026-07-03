package t3digitalgroup.vehnixauto.server.app.user.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "refresh_tokens")
class RefreshToken(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("expires_at")
    val expiresAt:  LocalDateTime = LocalDateTime.now(),
    @Column("hashed_token")
    val hashedToken: String,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)