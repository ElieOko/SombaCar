package t3digitalgroup.vehnixauto.server.app.car.domain.models

data class CarDocument(
    val carDocumentId: Long? = null,
    val carId: Long? = null,
    val documentId: Long,
    val document: Document? = null,
)
