package angrymiaucino.shortyurl.config

import angrymiaucino.shortyurl.repository.ShortLinkRepository
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class BloomFilterService(
    private val redisTemplate: ReactiveStringRedisTemplate,
    private val shortLinkRepository: ShortLinkRepository,
    @Value("\${bloom-filter.needs-fill}") private val needsPreload: Boolean = false,
) {
    companion object {
        private const val BLOOM_FILTER_SHORT_URL = "shortenurl-bloomfilter"
        private val logger = LoggerFactory.getLogger(BloomFilterService::class.java)
    }

    @PostConstruct
    fun initialize() {
        if (!needsPreload) {
            return
        }

        initBloomFilter()
            .then(preloadFromDbPaginated(0, 1000))
            .subscribe()


    }
    fun preloadFromDbPaginated(page: Int = 0, size: Int): Mono<Void> =
        shortLinkRepository.findShortCodesPaginated(page * size, size)
            .collectList()
            .flatMap { batch -> handle(batch, page, size) }


    private fun handle(batch: MutableList<String>, page: Int, size: Int) =
        if (batch.isEmpty()) {
            Mono.empty()
        } else {
            addBatchToBloomFilter(batch)
                .then(preloadFromDbPaginated(page + 1, size))
        }


    fun addBatchToBloomFilter(batch: List<String>): Mono<Void> {
        val script = RedisScript.of("for i=1,#ARGV do redis.call('BF.ADD', KEYS[1], ARGV[i]) end; return 1", Long::class.java)
        return redisTemplate.execute(script, listOf(BLOOM_FILTER_SHORT_URL), batch)
                .then()
    }

    fun initBloomFilter(): Mono<Boolean> {
        val script = """
                        if redis.call('EXISTS', KEYS[1]) == 0 then
                            redis.call('BF.RESERVE', KEYS[1], ARGV[1], ARGV[2])
                            return 1
                        else
                            return 0
                        end
                    """.trimIndent()

        val redisScript = RedisScript.of(script, Long::class.java)
        val keys = listOf(BLOOM_FILTER_SHORT_URL)
        val args = listOf("0.001", "100000")

        return redisTemplate.execute(redisScript, keys, args)
            .next()
            .map { it == 1L }
            .onErrorResume { error -> Mono.error(error) }
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

