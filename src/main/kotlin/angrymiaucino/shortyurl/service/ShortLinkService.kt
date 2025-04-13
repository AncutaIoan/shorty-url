package angrymiaucino.shortyurl.service

import angrymiaucino.shortyurl.config.BloomFilterService
import angrymiaucino.shortyurl.repository.ShortLinkRepository
import angrymiaucino.shortyurl.repository.entity.ShortLink
import angrymiaucino.shortyurl.router.reponse.ShortLinkResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class ShortLinkService(
    private val writeBufferService: WriteBufferService,
    private val bloomFilterService: BloomFilterService,
    private val repository: ShortLinkRepository,
    private val cacheService: CacheService
) {
    fun createShortLink(originalUrl: String, userId: UUID?): Mono<ShortLinkResponse> {
        val code = generateShortCode()

        return bloomFilterService.check(code)
            .map { exists -> checkCollisionAndUpdate(exists, code, originalUrl, userId) }
            .doOnNext { link -> writeBufferService.enqueue(link) }
            .flatMap { link -> bloomFilterService.add(link.shortCode).thenReturn(ShortLinkResponse(link.shortCode)) }
    }

    private fun checkCollisionAndUpdate(exists: Boolean, code: String, originalUrl: String, userId: UUID?) =
        shortLink(originalUrl, userId, code).takeIf { !exists }
            ?: shortLink(originalUrl, userId, generateShortCode())

    private fun shortLink(originalUrl: String, userId: UUID?, shortCode: String) =
        ShortLink(
            shortCode = shortCode,
            originalUrl = originalUrl,
            userId = userId
        )

    fun redirectToOriginalUrl(code: String): Mono<String> {
        return cacheService.get(code)
            .switchIfEmpty(findAndCache(code))
    }

    private fun findAndCache(code: String) =
        repository.findByShortCode(code)
            .flatMap { cacheService.put(code, it.originalUrl).then(Mono.just(it.originalUrl)) }

    private fun generateShortCode(): String =
        UUID.randomUUID().toString().take(8)
}
