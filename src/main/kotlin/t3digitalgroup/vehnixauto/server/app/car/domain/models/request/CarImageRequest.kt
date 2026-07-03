package t3digitalgroup.vehnixauto.server.app.car.domain.models.request

data class CarImageRequest(val image: String)

data class ImageRequest(val name: String)

data class ImageChangeRequest(
    val name: String,
    val old: String,
)
