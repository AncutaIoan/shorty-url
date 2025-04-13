package angrymiaucino.shortyurl.service

import angrymiaucino.shortyurl.repository.ShortLinkRepository
import angrymiaucino.shortyurl.repository.entity.ShortLink
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks

@Service
class WriteBufferService(
    private val repository: ShortLinkRepository
) {
    private val sink: Sinks.Many<ShortLink> = Sinks.many().unicast().onBackpressureBuffer()
    companion object {
        private val logger = LoggerFactory.getLogger(WriteBufferService::class.java)
    }
    init {
        startFlusher()
    }

    fun enqueue(link: ShortLink) {
        sink.tryEmitNext(link)
    }

    private fun startFlusher() {
        sink.asFlux()
            .flatMap { link ->
                repository.save(link)
                    .doOnError { e -> logger.error("WAL write failed for ${link.shortCode}: ${e.message}") }
                    .onErrorResume { Mono.empty() }
            }
            .subscribe()
    }
}
