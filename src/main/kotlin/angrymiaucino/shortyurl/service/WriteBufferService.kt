package angrymiaucino.shortyurl.service

import angrymiaucino.shortyurl.repository.ShortLinkRepository
import angrymiaucino.shortyurl.repository.entity.ShortLink
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks

@Service
class WriteBufferService(
    private val repository: ShortLinkRepository
) {
    private val sink: Sinks.Many<ShortLink> = Sinks.many().unicast().onBackpressureBuffer()

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
                    .doOnError { e -> println("WAL write failed for ${link.shortCode}: ${e.message}") }
                    .onErrorResume { Mono.empty() } // here will update with producer logic
            }
            .subscribe()
    }
}
