package angrymiaucino.shortyurl.router

import angrymiaucino.shortyurl.handler.ShortLinkHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class ShortLinkRouter {
    @Bean
    fun shortLinkRoutes(handler: ShortLinkHandler): RouterFunction<ServerResponse> = router {
        ("/api/short-links").nest {
            POST("", handler::create)
            GET("/{code}", handler::redirect)
        }
    }
}
