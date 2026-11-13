package t3digitalgroup.vehnixauto.server.app.delivery.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("part_deliveries")
class PartDeliveryEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("part_listing_id")
    val partListingId: Long? = null,
    @Column("buyer_user_id")
    val buyerUserId: Long,
    @Column("seller_user_id")
    var sellerUserId: Long? = null,
    @Column("courier_user_id")
    var courierUserId: Long? = null,
    @Column("delivery_address")
    var deliveryAddress: String,
    @Column("city")
    var city: String? = null,
    @Column("latitude")
    var latitude: Double? = null,
    @Column("longitude")
    var longitude: Double? = null,
    @Column("notes")
    var notes: String? = null,
    @Column("payment_reference")
    var paymentReference: String? = null,
    @Column("status")
    var status: String = "PENDING",
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)

@Table("part_delivery_tracking_events")
class PartDeliveryTrackingEventEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("delivery_id")
    val deliveryId: Long,
    @Column("user_id")
    val userId: Long? = null,
    @Column("latitude")
    val latitude: Double? = null,
    @Column("longitude")
    val longitude: Double? = null,
    @Column("tag")
    val tag: String = "Depart",
    @Column("is_active")
    val isActive: Boolean = true,
    @Column("message")
    val message: String,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
