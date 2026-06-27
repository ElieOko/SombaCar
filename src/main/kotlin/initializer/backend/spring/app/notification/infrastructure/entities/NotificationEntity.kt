package initializer.backend.spring.app.notification.infrastructure.entities

import initializer.backend.spring.app.notification.domain.models.TagType
import initializer.backend.spring.app.user.domain.models.UserDto
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("notifications")
class NotificationEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("user_id")
    val userId: Long? = null,
    @Column("title")
    val title: String,
    @Column("message")
    val message: String,
    @Column("is_active")
    var isActive: Boolean = true,
    @Column("tag")
    val tag: String = TagType.PART_INQUIRY.toString(),
    @Column("created")
    val created: LocalDateTime = LocalDateTime.now(),
    val user: UserDto? = null
)
