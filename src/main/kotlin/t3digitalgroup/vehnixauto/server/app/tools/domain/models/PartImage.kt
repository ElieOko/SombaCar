package t3digitalgroup.vehnixauto.server.app.tools.domain.models

data class PartImage(
    val partImageId: Long? = null,
    var partListingId: Long? = null,
    var name: String = "",
    var path: String = "",
)
