package t3digitalgroup.vehnixauto.server.app.sale.infrastructure.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import t3digitalgroup.vehnixauto.server.utils.OfferStatus
import t3digitalgroup.vehnixauto.server.utils.OfferType
import java.time.LocalDateTime

@Table(name = "sale_offers")
class SaleOfferEntity(
    @Id
    @Column("id")
    val saleOfferId: Long? = null,
    @Column("offer_type")
    var offerType: String = OfferType.PART.name,
    @Column("title")
    var title: String,
    @Column("description")
    var description: String? = null,
    @Column("price")
    var price: String,
    @Column("devise")
    var devise: String = "USD",
    @Column("linked_listing_id")
    var linkedListingId: Long? = null,
    @Column("status")
    var status: String = OfferStatus.ACTIVE.name,
    @Column("created_by")
    val createdBy: Long? = null,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
