package angrymiaucino.shortyurl.repository

import angrymiaucino.shortyurl.repository.entity.ShortLink
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface ShortLinkRepository : ReactiveCrudRepository<ShortLink, UUID> {
    fun findByShortCode(shortCode: String): Mono<ShortLink>
    fun findAllByUserId(userId: UUID): Flux<ShortLink>
    fun deleteByShortCode(shortCode: String): Mono<Long>
}
