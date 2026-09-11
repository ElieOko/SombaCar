package t3digitalgroup.vehnixauto.server.app.message.domain.models

data class MessageAttachment(
    val attachmentId: Long? = null,
    val messageId: Long? = null,
    val name: String,
    val path: String,
    val mimeType: String? = null,
)
