package t3digitalgroup.vehnixauto.server.app.cart.infrastructure.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import t3digitalgroup.vehnixauto.server.app.cart.infrastructure.entities.CartItemEntity

interface CartItemRepository : CoroutineCrudRepository<CartItemEntity, Long> {
    @Query(
        """
        SELECT * FROM cart_items
        WHERE user_id = :userId AND is_active = TRUE
        ORDER BY created_at DESC
        """
    )
    suspend fun findActiveByUserId(userId: Long): Flow<CartItemEntity>

    @Query(
        """
        SELECT * FROM cart_items
        WHERE user_id = :userId AND is_active = FALSE
        ORDER BY updated_at DESC
        """
    )
    suspend fun findInactiveByUserId(userId: Long): Flow<CartItemEntity>

    @Query(
        """
        SELECT * FROM cart_items
        WHERE user_id = :userId AND is_active = TRUE AND payment_reference IS NULL
        ORDER BY created_at DESC
        """
    )
    suspend fun findCheckoutReadyByUserId(userId: Long): Flow<CartItemEntity>

    @Query(
        """
        SELECT * FROM cart_items
        WHERE payment_reference = :paymentReference
        ORDER BY created_at DESC
        """
    )
    suspend fun findByPaymentReference(paymentReference: String): Flow<CartItemEntity>

    @Query(
        """
        SELECT * FROM cart_items
        WHERE user_id = :userId AND tools_id = :toolsId AND is_active = TRUE AND payment_reference IS NULL
        LIMIT 1
        """
    )
    suspend fun findActiveByUserIdAndToolsId(userId: Long, toolsId: Long): CartItemEntity?
}
