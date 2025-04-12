package angrymiaucino.shortyurl.controller.request

import java.util.*

data class CreateShortLinkRequest(
    val originalUrl: String,
    val userId: UUID? = null
)
