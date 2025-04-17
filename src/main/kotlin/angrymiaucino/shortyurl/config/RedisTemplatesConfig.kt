package angrymiaucino.shortyurl.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisTemplatesConfig {
    @Bean
    fun reactiveRedisTemplate(redisConnectionFactory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, ByteArray> {
        val redisSerializationContext =
            RedisSerializationContext.newSerializationContext<String, ByteArray>(StringRedisSerializer())
            .value(RedisSerializer.byteArray())
            .build()
        return ReactiveRedisTemplate(redisConnectionFactory, redisSerializationContext)
    }
}