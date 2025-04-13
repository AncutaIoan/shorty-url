package angrymiaucino.shortyurl.router.request

import java.util.*

data class CreateShortLinkRequest(
    val originalUrl: String,
    val userId: UUID? = null
)
