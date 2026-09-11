package t3digitalgroup.vehnixauto.server.app.mechanic.domain.models

data class MechanicImage(
    val mechanicImageId: Long? = null,
    var mechanicId: Long? = null,
    var name: String = "",
    var path: String = "",
)
