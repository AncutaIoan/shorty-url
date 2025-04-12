package angrymiaucino.shortyurl.config

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

//@Configuration
//@EnableConfigurationProperties(RedisProperties::class)
//class RedisConfig {
//
//    @Bean
//    fun redisConnectionFactory(props: RedisProperties): ReactiveRedisConnectionFactory {
//        return LettuceConnectionFactory(props.host, props.port)
//    }
//
//    @Bean
//    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, String> {
//        val serializer = StringRedisSerializer()
//        val context = RedisSerializationContext
//            .newSerializationContext<String, String>(serializer)
//            .key(serializer)
//            .value(serializer)
//            .hashKey(serializer)
//            .hashValue(serializer)
//            .build()
//        return ReactiveRedisTemplate(factory, context)
//    }
//}
