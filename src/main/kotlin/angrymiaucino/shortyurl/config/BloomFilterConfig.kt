package angrymiaucino.shortyurl.config

import angrymiaucino.shortyurl.repository.ShortLinkRepository
import io.lettuce.core.RedisCommandExecutionException
import jakarta.annotation.PostConstruct
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class BloomFilterService(
    private val redisTemplate: ReactiveStringRedisTemplate,
    private val shortLinkRepository: ShortLinkRepository
) {
    companion object {
        private const val BLOOM_FILTER_SHORT_URL = "shortenurl-bloomfilter"
    }

    @PostConstruct
    fun initialize() {
        initBloomFilter()
            .then(preloadFromDb(shortLinkRepository.findAll().map { it.shortCode }))
            .subscribe()
    }

    fun preloadFromDb(urls: Flux<String>): Mono<Void> {
        return urls
            .flatMap { add(it) }
            .then()
    }

    fun initBloomFilter(): Mono<Boolean> {
        val script = """
                                if redis.call('EXISTS', KEYS[1]) == 0 then
                                    redis.call('BF.RESERVE', KEYS[1], ARGV[1], ARGV[2])
                                    return true
                                else
                                    return false
                                end
                            """.trimIndent()

        val redisScript = RedisScript.of(script, String::class.java)
        val keys = listOf(BLOOM_FILTER_SHORT_URL)
        val args = listOf("0.001", "100000") // error rate and expected number of items

        return redisTemplate.execute(redisScript, keys, args)
            .next()
            .map { it == "true" }
            .onErrorResume { error ->
                // Handle any errors in case of other issues
                Mono.error(error)
            }
    }


    fun add(data: String): Mono<Boolean> {
        val script = RedisScript.of("return redis.call('BF.ADD', KEYS[1], ARGV[1])", Long::class.java)
        return redisTemplate.execute(script, listOf(BLOOM_FILTER_SHORT_URL), listOf(data))
            .next()
            .map { it == 1L } // Check if the result is 1 (added successfully)
    }

    fun check(data: String): Mono<Boolean> {
        val script = RedisScript.of("return redis.call('BF.EXISTS', KEYS[1], ARGV[1])", Long::class.java)
        return redisTemplate.execute(script, listOf(BLOOM_FILTER_SHORT_URL), listOf(data))
            .next()
            .map { it == 1L } // Check if the result is 1 (exists)
    }
}
