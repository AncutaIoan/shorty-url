package angrymiaucino.shortyurl.service

import angrymiaucino.shortyurl.config.BloomFilterService
import angrymiaucino.shortyurl.repository.ShortLinkRepository
import angrymiaucino.shortyurl.repository.entity.ShortLink
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.*

@Service
class ShortLinkService(
    private val writeBufferService: WriteBufferService,
    private val bloomFilterService: BloomFilterService,
    private val repository: ShortLinkRepository
) {
    fun createShortLink(originalUrl: String, userId: UUID?): Mono<ShortLink> {
        val code = generateShortCode()

        return bloomFilterService.check(code)
            .map { exists -> checkCollisionAndUpdate(exists, code, originalUrl, userId) }
            .doOnNext { link -> writeBufferService.enqueue(link) }
            .flatMap { link -> bloomFilterService.add(link.shortCode).thenReturn(link) }
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

    fun getOriginalUrl(shortCode: String): Mono<String> =
        repository.findByShortCode(shortCode)
            .map { it.originalUrl }
            .switchIfEmpty { Mono.error(RuntimeException("Original URL is empty!")) }

    private fun generateShortCode(): String =
        UUID.randomUUID().toString().take(8)
}
