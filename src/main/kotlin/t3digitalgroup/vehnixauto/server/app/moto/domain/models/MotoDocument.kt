package t3digitalgroup.vehnixauto.server.app.moto.domain.models

import t3digitalgroup.vehnixauto.server.app.car.domain.models.Document

data class MotoDocument(
    val motoDocumentId: Long? = null,
    val motoId: Long? = null,
    val documentId: Long,
    val document: Document? = null,
)
