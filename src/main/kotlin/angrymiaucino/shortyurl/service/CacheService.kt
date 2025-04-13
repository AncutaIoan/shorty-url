package angrymiaucino.shortyurl.service

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CacheService(private val redisTemplate: ReactiveRedisTemplate<String, String>) {

    private val valueOperations: ReactiveValueOperations<String, String> = redisTemplate.opsForValue()

    fun get(code: String): Mono<String> {
        return valueOperations.get(code)
    }

    fun put(code: String, originalUrl: String): Mono<Boolean> {
        return valueOperations.set(code, originalUrl)
    }

    fun delete(code: String): Mono<Boolean> {
        return redisTemplate.opsForValue().getAndDelete(code).map { it != null }
    }
}
