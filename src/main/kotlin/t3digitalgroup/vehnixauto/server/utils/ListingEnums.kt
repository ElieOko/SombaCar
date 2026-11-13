package t3digitalgroup.vehnixauto.server.utils

enum class ListingType {
    SALE,
    RENT,
    EXCHANGE
}

enum class ListingStatus {
    /** Disponible à la vente / location */
    ACTIVE,
    /** En cours de négociation ou de réservation */
    NEGOTIATING,
    SOLD,
    RENTED,
    EXCHANGED,
    INACTIVE,
    BANNED,
}

enum class ItemCondition {
    NEW,
    USED
}

enum class TransmissionType {
    MANUAL,
    AUTOMATIC,
    CVT,
    OTHER,
}

enum class TrackingTag {
    DEPART,
    EN_ROUTE,
    ARRIVE,
    PAUSE,
    FIN,
}

enum class VehicleKind {
    CAR,
    MOTO,
}

enum class RentalDurationUnit {
    DAYS,
    WEEKS,
    MONTHS,
}

enum class RentalRequestStatus {
    PENDING,
    CONFIRMED,
    PAID,
    CANCELLED,
}

enum class DeliveryStatus {
    PENDING,
    ASSIGNED,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED,
}
