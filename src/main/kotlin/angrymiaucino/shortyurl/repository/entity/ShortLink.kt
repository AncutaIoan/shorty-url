package angrymiaucino.shortyurl.repository.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("short_links")
data class ShortLink(
    @Id
    val id: UUID? = null,
    val shortCode: String,
    val originalUrl: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime? = null,
    val clicks: Int = 0,
    val userId: UUID? = null,
    val isActive: Boolean = true
)
