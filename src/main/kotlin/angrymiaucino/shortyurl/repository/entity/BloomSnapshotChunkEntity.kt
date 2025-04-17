package angrymiaucino.shortyurl.repository.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime


@Table("bloom_snapshot_chunks")
data class BloomSnapshotChunkEntity(
    @Id
    val id: Long? = null,
    val name: String,
    val snapshot: ByteArray,
    val chunkId: Int,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)