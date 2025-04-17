package angrymiaucino.shortyurl.repository

import angrymiaucino.shortyurl.repository.entity.BloomSnapshotChunkEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*

interface BloomSnapshotChunkRepository: ReactiveCrudRepository<BloomSnapshotChunkEntity, UUID> {
}