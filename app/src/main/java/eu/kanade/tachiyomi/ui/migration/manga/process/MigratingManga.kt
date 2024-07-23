package eu.mkonic.tachiyomi.ui.migration.manga.process

import eu.mkonic.tachiyomi.data.database.DatabaseHelper
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.source.Source
import eu.mkonic.tachiyomi.source.SourceManager
import eu.mkonic.tachiyomi.util.view.DeferredField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlin.coroutines.CoroutineContext

class MigratingManga(
    private val db: DatabaseHelper,
    private val sourceManager: SourceManager,
    val mangaId: Long,
    parentContext: CoroutineContext,
) {
    val searchResult = DeferredField<Long?>()

    // <MAX, PROGRESS>
    val progress = ConflatedBroadcastChannel(1 to 0)

    val migrationJob = parentContext + SupervisorJob() + Dispatchers.Default

    var migrationStatus: Int = MigrationStatus.RUNNUNG

    @Volatile
    private var manga: Manga? = null
    suspend fun manga(): Manga? {
        if (manga == null) manga = db.getManga(mangaId).executeAsBlocking()
        return manga
    }

    suspend fun mangaSource(): Source {
        return sourceManager.getOrStub(manga()?.source ?: -1)
    }

    fun toModal(): MigrationProcessItem {
        // Create the model object.
        return MigrationProcessItem(this)
    }
}

class MigrationStatus {
    companion object {
        val RUNNUNG = 0
        val MANGA_FOUND = 1
        val MANGA_NOT_FOUND = 2
    }
}
