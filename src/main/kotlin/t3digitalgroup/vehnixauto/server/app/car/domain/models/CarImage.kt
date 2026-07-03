package t3digitalgroup.vehnixauto.server.app.car.domain.models

data class CarImage(
    val carImageId: Long? = null,
    var carId: Long? = null,
    var name: String = "",
    var path: String = "",
)
