package t3digitalgroup.vehnixauto.server.adaptater.provider.redis

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisStorage(
    private val redisTemplate: StringRedisTemplate,
) {
    private val log = LoggerFactory.getLogger(RedisStorage::class.java)

    private fun redisKey(key: String) = "vehnixauto-$key"

    fun getRedisData(key: String = "foo"): String? {
        val value = redisTemplate.opsForValue().get(redisKey(key))
        log.info("Value at $key: $value")
        return value
    }

    fun delete(key: String) {
        redisTemplate.delete(redisKey(key))
    }

    fun storeRedisData(key: String, value: String, time: Long = 5) {
        redisTemplate.opsForValue().set(redisKey(key), value, time, TimeUnit.MINUTES)
    }
}
