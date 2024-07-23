package eu.mkonic.tachiyomi.ui.migration

import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.source.CatalogueSource
import eu.mkonic.tachiyomi.ui.source.globalsearch.GlobalSearchItem
import eu.mkonic.tachiyomi.ui.source.globalsearch.GlobalSearchMangaItem
import eu.mkonic.tachiyomi.ui.source.globalsearch.GlobalSearchPresenter

class SearchPresenter(
    initialQuery: String? = "",
    private val manga: Manga,
    sources: List<CatalogueSource>? = null,
) : GlobalSearchPresenter(initialQuery, sourcesToUse = sources) {

    override fun getEnabledSources(): List<CatalogueSource> {
        // Put the source of the selected manga at the top
        return super.getEnabledSources()
            .sortedByDescending { it.id == manga.source }
    }

    override fun createCatalogueSearchItem(source: CatalogueSource, results: List<GlobalSearchMangaItem>?): GlobalSearchItem {
        // Set the catalogue search item as highlighted if the source matches that of the selected manga
        return GlobalSearchItem(source, results, source.id == manga.source)
    }
}
