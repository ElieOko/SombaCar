package t3digitalgroup.vehnixauto.server.app.notification.infrastructure.entities

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.*
import java.time.*

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
