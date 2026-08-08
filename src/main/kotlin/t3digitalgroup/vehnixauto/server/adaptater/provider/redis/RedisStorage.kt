package t3digitalgroup.vehnixauto.server.adaptater.provider.redis

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import t3digitalgroup.vehnixauto.server.VehnixAutoApplication

import java.util.concurrent.TimeUnit

class RedisStorage {
   private val log = LoggerFactory.getLogger(VehnixAutoApplication::class.java)
    fun getRedisData(key : String = "foo") : String?{
        val connectionFactory = LettuceConnectionFactory()
        val template = RedisTemplate<String?, String?>()
        connectionFactory.afterPropertiesSet()
        template.connectionFactory = connectionFactory
        template.defaultSerializer = StringRedisSerializer.UTF_8
        template.afterPropertiesSet()
        log.info("Value at $key:" + template.opsForValue().get("vehnixauto-$key"))
        template.opsForValue().get("vehnixauto-$key")
//        connectionFactory.destroy()
        return template.opsForValue().get("vehnixauto-$key")
    }
    fun delete(key: String){
        val connectionFactory = LettuceConnectionFactory()
        val template = RedisTemplate<String?, String?>()
        connectionFactory.afterPropertiesSet()
        template.connectionFactory = connectionFactory
        template.defaultSerializer = StringRedisSerializer.UTF_8
        template.afterPropertiesSet()
        template.delete("vehnixauto-$key")
        connectionFactory.destroy()
    }
    fun storeRedisData(key: String, value : String, time : Long = 5){
        val connectionFactory = LettuceConnectionFactory()
        val template = RedisTemplate<String?, String?>()
        connectionFactory.afterPropertiesSet()
        template.connectionFactory = connectionFactory
        template.defaultSerializer = StringRedisSerializer.UTF_8
        template.afterPropertiesSet()
        template.opsForValue().set("vehnixauto-$key", value,time, TimeUnit.MINUTES)
        connectionFactory.destroy()
    }
}