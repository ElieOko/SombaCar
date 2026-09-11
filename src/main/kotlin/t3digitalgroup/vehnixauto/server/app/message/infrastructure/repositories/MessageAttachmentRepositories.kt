package t3digitalgroup.vehnixauto.server.app.message.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.CarListingMessageAttachmentEntity
import t3digitalgroup.vehnixauto.server.app.message.infrastructure.entities.SupportMessageAttachmentEntity

interface CarListingMessageAttachmentRepository :
    CoroutineCrudRepository<CarListingMessageAttachmentEntity, Long> {
    fun findByMessageIdIn(messageIds: List<Long>): Flow<CarListingMessageAttachmentEntity>
}

interface SupportMessageAttachmentRepository :
    CoroutineCrudRepository<SupportMessageAttachmentEntity, Long> {
    fun findByMessageIdIn(messageIds: List<Long>): Flow<SupportMessageAttachmentEntity>
}
