package eu.mkonic.tachiyomi.ui.migration.manga.process

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import coil.Coil
import coil.request.ImageRequest
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.data.database.DatabaseHelper
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.data.image.coil.CoverViewTarget
import eu.mkonic.tachiyomi.data.image.coil.MangaCoverFetcher
import eu.mkonic.tachiyomi.databinding.MangaGridItemBinding
import eu.mkonic.tachiyomi.databinding.MigrationProcessItemBinding
import eu.mkonic.tachiyomi.source.Source
import eu.mkonic.tachiyomi.source.SourceManager
import eu.mkonic.tachiyomi.ui.base.holder.BaseFlexibleViewHolder
import eu.mkonic.tachiyomi.ui.library.setFreeformCoverRatio
import eu.mkonic.tachiyomi.ui.manga.MangaDetailsController
import eu.mkonic.tachiyomi.util.system.launchUI
import eu.mkonic.tachiyomi.util.view.setCards
import eu.mkonic.tachiyomi.util.view.setVectorCompat
import eu.mkonic.tachiyomi.util.view.withFadeTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uy.kohesive.injekt.injectLazy
import java.text.DecimalFormat

class MigrationProcessHolder(
    private val view: View,
    private val adapter: MigrationProcessAdapter,
) : BaseFlexibleViewHolder(view, adapter) {

    private val db: DatabaseHelper by injectLazy()
    private val sourceManager: SourceManager by injectLazy()
    private var item: MigrationProcessItem? = null
    private val binding = MigrationProcessItemBinding.bind(view)

    init {
        // We need to post a Runnable to show the popup to make sure that the PopupMenu is
        // correctly positioned. The reason being that the view may change position before the
        // PopupMenu is shown.
        binding.migrationMenu.setOnClickListener { it.post { showPopupMenu(it) } }
        binding.skipManga.setOnClickListener { it.post { adapter.removeManga(flexibleAdapterPosition) } }
        arrayOf(binding.migrationMangaCardFrom, binding.migrationMangaCardTo).forEach {
            setCards(adapter.showOutline, it.card, it.unreadDownloadBadge.badgeView)
        }
        binding.migrationMangaCardFrom.title.maxLines = 1
        binding.migrationMangaCardTo.title.maxLines = 1
    }

    fun bind(item: MigrationProcessItem) {
        this.item = item
        launchUI {
            binding.migrationMangaCardFrom.setFreeformCoverRatio(item.manga.manga())
            binding.migrationMangaCardTo.setFreeformCoverRatio(null)

            val manga = item.manga.manga()
            val source = item.manga.mangaSource()

            binding.migrationMenu.setVectorCompat(
                R.drawable.ic_more_vert_24dp,
                R.attr.colorOnBackground,
            )
            binding.skipManga.setVectorCompat(
                R.drawable.ic_close_24dp,
                R.attr.colorOnBackground,
            )
            binding.migrationMenu.isInvisible = true
            binding.skipManga.isVisible = true
            binding.migrationMangaCardTo.resetManga()
            if (manga != null) {
                withContext(Dispatchers.Main) {
                    binding.migrationMangaCardFrom.attachManga(manga, source)
                    binding.migrationMangaCardFrom.root.setOnClickListener {
                        adapter.controller.router.pushController(
                            MangaDetailsController(
                                manga,
                                true,
                            ).withFadeTransaction(),
                        )
                    }
                }

                /*launchUI {
                    item.manga.progress.asFlow().collect { (max, progress) ->
                        withContext(Dispatchers.Main) {
                            binding.migrationMangaCardTo.search_progress.let { progressBar ->
                                progressBar.max = max
                                progressBar.progress = progress
                            }
                        }
                    }
                }*/

                val searchResult = item.manga.searchResult.get()?.let {
                    db.getManga(it).executeAsBlocking()
                }
                val resultSource = searchResult?.source?.let {
                    sourceManager.get(it)
                }
                withContext(Dispatchers.Main) {
                    if (item.manga.mangaId != this@MigrationProcessHolder.item?.manga?.mangaId || item.manga.migrationStatus == MigrationStatus.RUNNUNG) {
                        return@withContext
                    }
                    if (searchResult != null && resultSource != null) {
                        binding.migrationMangaCardTo.attachManga(searchResult, resultSource)
                        binding.migrationMangaCardTo.root.setOnClickListener {
                            adapter.controller.router.pushController(
                                MangaDetailsController(
                                    searchResult,
                                    true,
                                ).withFadeTransaction(),
                            )
                        }
                    } else {
                        binding.migrationMangaCardTo.coverThumbnail.setImageDrawable(null)
                        binding.migrationMangaCardTo.progress.isVisible = false
                        binding.migrationMangaCardTo.title.text =
                            view.context.getString(R.string.no_alternatives_found)
                    }
                    binding.migrationMenu.isVisible = true
                    binding.skipManga.isVisible = false
                    adapter.sourceFinished()
                }
            }
        }
    }

    private fun MangaGridItemBinding.resetManga() {
        progress.isVisible = true
        coverThumbnail.setImageDrawable(null)
        compactTitle.text = ""
        title.text = ""
        subtitle.text = ""
        unreadDownloadBadge.badgeView.setChapters(null)
        (root.layoutParams as ConstraintLayout.LayoutParams).verticalBias = 0.5f
        subtitle.text = ""
        root.setOnClickListener(null)
    }

    private fun MangaGridItemBinding.attachManga(manga: Manga, source: Source) {
        (root.layoutParams as ConstraintLayout.LayoutParams).verticalBias = 1f
        progress.isVisible = false

        val request = ImageRequest.Builder(view.context).data(manga)
            .target(CoverViewTarget(coverThumbnail, progress))
            .setParameter(MangaCoverFetcher.useCustomCover, false)
            .build()
        Coil.imageLoader(view.context).enqueue(request)

        compactTitle.isVisible = true
        gradient.isVisible = true
        compactTitle.text = if (manga.title.isBlank()) {
            view.context.getString(R.string.unknown)
        } else {
            manga.title
        }

        gradient.isVisible = true
        title.text = source.toString()

        val mangaChapters = db.getChapters(manga).executeAsBlocking()
        unreadDownloadBadge.badgeView.setChapters(mangaChapters.size)
        val latestChapter = mangaChapters.maxOfOrNull { it.chapter_number } ?: -1f

        if (latestChapter > 0f) {
            subtitle.text = root.context.getString(
                R.string.latest_,
                DecimalFormat("#.#").format(latestChapter),
            )
        } else {
            subtitle.text = root.context.getString(
                R.string.latest_,
                root.context.getString(R.string.unknown),
            )
        }
    }

    private fun showPopupMenu(view: View) {
        val item = adapter.getItem(flexibleAdapterPosition) ?: return

        // Create a PopupMenu, giving it the clicked view for an anchor
        val popup = PopupMenu(view.context, view)

        // Inflate our menu resource into the PopupMenu's Menu
        popup.menuInflater.inflate(R.menu.migration_single, popup.menu)

        val mangas = item.manga

        popup.menu.findItem(R.id.action_search_manually).isVisible = true
        // Hide download and show delete if the chapter is downloaded
        if (mangas.searchResult.content != null) {
            popup.menu.findItem(R.id.action_migrate_now).isVisible = true
            popup.menu.findItem(R.id.action_copy_now).isVisible = true
        }

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener { menuItem ->
            adapter.menuItemListener.onMenuItemClick(flexibleAdapterPosition, menuItem)
            true
        }

        // Finally show the PopupMenu
        popup.show()
    }
}
