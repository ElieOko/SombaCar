package initializer.backend.spring.app.notification.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("notification_systems")
data class NotificationSystemEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("title")
    val title: String,
    @Column("description")
    val description: String,
    @Column("date_created")
    val dateCreated: LocalDate = LocalDate.now()
)
