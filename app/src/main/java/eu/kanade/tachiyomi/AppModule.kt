package eu.mkonic.tachiyomi

import android.app.Application
import androidx.core.content.ContextCompat
import eu.mkonic.tachiyomi.data.cache.ChapterCache
import eu.mkonic.tachiyomi.data.cache.CoverCache
import eu.mkonic.tachiyomi.data.database.DatabaseHelper
import eu.mkonic.tachiyomi.data.download.DownloadManager
import eu.mkonic.tachiyomi.data.library.CustomMangaManager
import eu.mkonic.tachiyomi.data.preference.AndroidPreferenceStore
import eu.mkonic.tachiyomi.data.preference.PreferenceStore
import eu.mkonic.tachiyomi.data.preference.PreferencesHelper
import eu.mkonic.tachiyomi.data.track.TrackManager
import eu.mkonic.tachiyomi.data.track.TrackPreferences
import eu.mkonic.tachiyomi.extension.ExtensionManager
import eu.mkonic.tachiyomi.extension.util.TrustExtension
import eu.mkonic.tachiyomi.network.JavaScriptEngine
import eu.mkonic.tachiyomi.network.NetworkHelper
import eu.mkonic.tachiyomi.source.SourceManager
import eu.mkonic.tachiyomi.util.chapter.ChapterFilter
import eu.mkonic.tachiyomi.util.manga.MangaShortcutManager
import kotlinx.serialization.json.Json
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class AppModule(val app: Application) : InjektModule {

    override fun InjektRegistrar.registerInjectables() {
        addSingleton(app)

        addSingletonFactory<PreferenceStore> {
            AndroidPreferenceStore(app)
        }

        addSingletonFactory { PreferencesHelper(app) }

        addSingletonFactory { TrackPreferences(get()) }

        addSingletonFactory { DatabaseHelper(app) }

        addSingletonFactory { ChapterCache(app) }

        addSingletonFactory { CoverCache(app) }

        addSingletonFactory { NetworkHelper(app) }

        addSingletonFactory { JavaScriptEngine(app) }

        addSingletonFactory { SourceManager(app, get()) }
        addSingletonFactory { ExtensionManager(app) }

        addSingletonFactory { DownloadManager(app) }

        addSingletonFactory { CustomMangaManager(app) }

        addSingletonFactory { TrackManager(app) }

        addSingletonFactory {
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }
        }

        addSingletonFactory { ChapterFilter() }

        addSingletonFactory { MangaShortcutManager() }

        addSingletonFactory { TrustExtension(get()) }

        // Asynchronously init expensive components for a faster cold start

        ContextCompat.getMainExecutor(app).execute {
            get<PreferencesHelper>()

            get<NetworkHelper>()

            get<SourceManager>()

            get<DatabaseHelper>()

            get<DownloadManager>()

            get<CustomMangaManager>()
        }
    }
}
