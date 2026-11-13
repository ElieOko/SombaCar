package t3digitalgroup.vehnixauto.server.app.rental.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Table("rental_requests")
class RentalRequestEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("vehicle_type")
    val vehicleType: String,
    @Column("vehicle_listing_id")
    val vehicleListingId: Long,
    @Column("client_user_id")
    val clientUserId: Long,
    @Column("driver_user_id")
    var driverUserId: Long? = null,
    @Column("start_date")
    val startDate: LocalDate,
    @Column("end_date")
    val endDate: LocalDate,
    @Column("duration_unit")
    val durationUnit: String = "DAYS",
    @Column("duration_value")
    val durationValue: Int = 1,
    @Column("destinations")
    val destinations: String? = null,
    @Column("planned_route")
    val plannedRoute: String? = null,
    @Column("motive")
    val motive: String? = null,
    @Column("additional_info")
    val additionalInfo: String? = null,
    @Column("quoted_amount")
    var quotedAmount: String? = null,
    @Column("devise")
    var devise: String? = "USD",
    @Column("payment_link")
    var paymentLink: String? = null,
    @Column("payment_reference")
    var paymentReference: String? = null,
    @Column("status")
    var status: String = "PENDING",
    @Column("support_thread_id")
    var supportThreadId: Long? = null,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
