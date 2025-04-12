package angrymiaucino.shortyurl.controller

import angrymiaucino.shortyurl.controller.request.CreateShortLinkRequest
import angrymiaucino.shortyurl.service.ShortLinkService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/short-links")
class ShortLinkController(
    private val service: ShortLinkService
) {

    @PostMapping
    fun create(@RequestBody request: CreateShortLinkRequest) =
        service.createShortLink(request.originalUrl, request.userId)

    @GetMapping("/{code}")
    fun redirect(@PathVariable code: String) =
        service.getOriginalUrl(code)
}

