package t3digitalgroup.vehnixauto.server.app.garage.domain.models

data class GarageImage(
    val garageImageId: Long? = null,
    var garageId: Long? = null,
    var name: String = "",
    var path: String = "",
)
