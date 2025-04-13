package angrymiaucino.shortyurl.handler

import angrymiaucino.shortyurl.router.request.CreateShortLinkRequest
import angrymiaucino.shortyurl.service.ShortLinkService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class ShortLinkHandler(
    private val service: ShortLinkService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ShortLinkHandler::class.java)
    }

    fun create(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(CreateShortLinkRequest::class.java)
            .flatMap { req -> service.createShortLink(req.originalUrl, req.userId) }
            .flatMap { result -> ServerResponse.ok().bodyValue(result) }
            .onErrorResume { ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build() }
    }

    fun redirect(request: ServerRequest): Mono<ServerResponse> {
        val code = request.pathVariable("code")
        return service.redirectToOriginalUrl(code)
            .flatMap { originalUrl -> buildFoundResponse(originalUrl) }
            .onErrorResume { ServerResponse.notFound().build() }
    }

    private fun buildFoundResponse(originalUrl: String) =
        ServerResponse.status(HttpStatus.FOUND)
            .header("Location", originalUrl)
            .build()
}



