package t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.*
import t3digitalgroup.vehnixauto.server.app.message.domain.models.CarListingThreadStatus
import java.time.LocalDateTime

@Table(name = "car_listing_threads")
class CarListingThreadEntity(
    @Id
    @Column("id")
    val threadId: Long? = null,
    @Column("car_listing_id")
    val carListingId: Long,
    @Column("buyer_id")
    val buyerId: Long,
    @Column("seller_id")
    val sellerId: Long,
    @Column("status")
    var status: String = CarListingThreadStatus.OPEN.name,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
