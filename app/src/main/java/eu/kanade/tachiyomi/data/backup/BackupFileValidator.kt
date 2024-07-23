package eu.mkonic.tachiyomi.data.backup

import android.content.Context
import android.net.Uri
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.data.track.TrackManager
import eu.mkonic.tachiyomi.source.SourceManager
import eu.mkonic.tachiyomi.util.BackupUtil
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class BackupFileValidator(
    private val sourceManager: SourceManager = Injekt.get(),
    private val trackManager: TrackManager = Injekt.get(),
) {

    /**
     * Checks for critical backup file data.
     *
     * @throws Exception if manga cannot be found.
     * @return List of missing sources or missing trackers.
     */
    fun validate(context: Context, uri: Uri): Results {
        val backup = try {
            BackupUtil.decodeBackup(context, uri)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }

        if (backup.backupManga.isEmpty()) {
            throw IllegalStateException(context.getString(R.string.backup_has_no_manga))
        }

        val sources = backup.backupSources.map { it.sourceId to it.name }.toMap()
        val missingSources = sources
            .filter { sourceManager.get(it.key) == null }
            .map { sourceManager.getOrStub(it.key).name }
            .sorted()

        val trackers = backup.backupManga
            .flatMap { it.tracking }
            .map { it.syncId }
            .distinct()
        val missingTrackers = trackers
            .mapNotNull { trackManager.getService(it) }
            .filter { !it.isLogged }
            .map { context.getString(it.nameRes()) }
            .sorted()

        return Results(missingSources, missingTrackers)
    }

    data class Results(val missingSources: List<String>, val missingTrackers: List<String>)
}
