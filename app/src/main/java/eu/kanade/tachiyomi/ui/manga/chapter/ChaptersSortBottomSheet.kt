package eu.mkonic.tachiyomi.ui.manga.chapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.databinding.ChapterSortBottomSheetBinding
import eu.mkonic.tachiyomi.ui.manga.MangaDetailsController
import eu.mkonic.tachiyomi.util.chapter.ChapterUtil
import eu.mkonic.tachiyomi.util.system.dpToPx
import eu.mkonic.tachiyomi.util.system.materialAlertDialog
import eu.mkonic.tachiyomi.util.system.rootWindowInsetsCompat
import eu.mkonic.tachiyomi.util.system.setNegativeStateItems
import eu.mkonic.tachiyomi.util.view.checkHeightThen
import eu.mkonic.tachiyomi.util.view.setBottomEdge
import eu.mkonic.tachiyomi.widget.E2EBottomSheetDialog
import eu.mkonic.tachiyomi.widget.SortTextView
import kotlin.math.max

class ChaptersSortBottomSheet(controller: MangaDetailsController) :
    E2EBottomSheetDialog<ChapterSortBottomSheetBinding>(controller.activity!!) {

    val activity = controller.activity!!

    private val presenter = controller.presenter

    override fun createBinding(inflater: LayoutInflater) = ChapterSortBottomSheetBinding.inflate(inflater)
    init {
        val height = activity.window.decorView.rootWindowInsetsCompat
            ?.getInsetsIgnoringVisibility(systemBars())?.bottom ?: 0
        sheetBehavior.peekHeight = 470.dpToPx + height

        sheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, progress: Float) {
                    if (progress.isNaN()) {
                        binding.pill.alpha = 0f
                    } else {
                        binding.pill.alpha = (1 - max(0f, progress)) * 0.25f
                    }
                }

                override fun onStateChanged(p0: View, state: Int) {
                    if (state == BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior.skipCollapsed = true
                    }
                }
            },
        )
    }

    override fun onStart() {
        super.onStart()
        sheetBehavior.skipCollapsed = true
    }

    /**
     * Called when the sheet is created. It initializes the listeners and values of the preferences.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initGeneralPreferences()
        setBottomEdge(binding.hideTitles, activity)
        binding.settingsScrollView.checkHeightThen {
            val isScrollable =
                binding.settingsScrollView.height < binding.sortLayout.height +
                    binding.settingsScrollView.paddingTop + binding.settingsScrollView.paddingBottom
            // making the view gone somehow breaks the layout so lets make it invisible
            binding.pill.isInvisible = isScrollable
        }
    }

    private fun initGeneralPreferences() {
        binding.chapterFilterLayout.root.setCheckboxes(presenter.manga, presenter.preferences)
        checkIfFilterMatchesDefault(binding.chapterFilterLayout.root)
        binding.chapterFilterLayout.root.setOnCheckedChangeListener(::setFilters)

        binding.byChapterNumber.state = SortTextView.State.NONE
        binding.byUploadDate.state = SortTextView.State.NONE
        binding.bySource.state = SortTextView.State.NONE

        val sortItem = when (presenter.sortingOrder()) {
            Manga.CHAPTER_SORTING_NUMBER -> binding.byChapterNumber
            Manga.CHAPTER_SORTING_UPLOAD_DATE -> binding.byUploadDate
            else -> binding.bySource
        }

        sortItem.state = if (presenter.sortDescending()) {
            SortTextView.State.DESCENDING
        } else {
            SortTextView.State.ASCENDING
        }

        checkIfSortMatchesDefault()
        binding.byChapterNumber.setOnSortChangeListener(::sortChanged)
        binding.byUploadDate.setOnSortChangeListener(::sortChanged)
        binding.bySource.setOnSortChangeListener(::sortChanged)

        binding.hideTitles.isChecked = presenter.manga.hideChapterTitle(presenter.preferences)

        binding.setAsDefaultSort.setOnClickListener {
            presenter.setGlobalChapterSort(
                presenter.manga.sorting,
                presenter.manga.sortDescending,
            )
            binding.setAsDefaultSort.isInvisible = true
            binding.resetAsDefaultSort.isInvisible = true
        }

        binding.resetAsDefaultSort.setOnClickListener {
            presenter.resetSortingToDefault()

            binding.byChapterNumber.state = SortTextView.State.NONE
            binding.byUploadDate.state = SortTextView.State.NONE
            binding.bySource.state = SortTextView.State.NONE

            val sortItemNew = when (presenter.sortingOrder()) {
                Manga.CHAPTER_SORTING_NUMBER -> binding.byChapterNumber
                Manga.CHAPTER_SORTING_UPLOAD_DATE -> binding.byUploadDate
                else -> binding.bySource
            }

            sortItemNew.state = if (presenter.sortDescending()) {
                SortTextView.State.DESCENDING
            } else {
                SortTextView.State.ASCENDING
            }
            binding.setAsDefaultSort.isInvisible = true
            binding.resetAsDefaultSort.isInvisible = true
        }
        setHideTitleListener()

        binding.chapterFilterLayout.setAsDefaultFilter.setOnClickListener {
            presenter.setGlobalChapterFilters(
                binding.chapterFilterLayout.showUnread.state,
                binding.chapterFilterLayout.showDownload.state,
                binding.chapterFilterLayout.showBookmark.state,
            )
            binding.chapterFilterLayout.setAsDefaultFilter.isInvisible = true
            binding.chapterFilterLayout.resetAsDefaultFilter.isInvisible = true
        }

        binding.chapterFilterLayout.resetAsDefaultFilter.setOnClickListener {
            presenter.resetFilterToDefault()

            binding.chapterFilterLayout.root.setCheckboxes(presenter.manga, presenter.preferences)
            binding.hideTitles.setOnCheckedChangeListener { _, _ -> }
            binding.hideTitles.isChecked = presenter.manga.hideChapterTitle(presenter.preferences)
            setHideTitleListener()
            binding.chapterFilterLayout.setAsDefaultFilter.isInvisible = true
            binding.chapterFilterLayout.resetAsDefaultFilter.isInvisible = true
        }
        binding.filterGroupsButton.isVisible = presenter.allChapterScanlators.size > 1

        binding.filterGroupsButton.setOnClickListener {
            val scanlators = presenter.allChapterScanlators.toList()
            val filteredScanlators =
                (
                    presenter.manga.filtered_scanlators?.let { ChapterUtil.getScanlators(it) }
                        ?.toMutableSet()
                        ?: mutableSetOf()
                    )
            val preselected = scanlators.map { it in filteredScanlators }.toBooleanArray()
            var alertDialog: AlertDialog? = null
            activity.materialAlertDialog()
                .setTitle(R.string.filter_groups)
                .setNegativeStateItems(scanlators, preselected) { _, pos, checked ->
                    if (checked) {
                        filteredScanlators.add(scanlators[pos])
                    } else {
                        filteredScanlators.remove(scanlators[pos])
                    }
                    alertDialog?.getButton(BUTTON_POSITIVE)?.isEnabled =
                        scanlators.size != filteredScanlators.size
                }
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.filter) { _, _ ->
                    presenter.setScanlatorFilter(filteredScanlators)
                }
                .setNeutralButton(R.string.reset) { _, _ ->
                    presenter.setScanlatorFilter(emptySet())
                }
                .show().apply {
                    alertDialog = this
                    setOnCancelListener { alertDialog = null }
                    setOnDismissListener { alertDialog = null }
                }
        }
    }

    private fun setHideTitleListener() {
        binding.hideTitles.setOnCheckedChangeListener { _, isChecked ->
            presenter.hideTitle(isChecked)
            checkIfFilterMatchesDefault(binding.chapterFilterLayout.root)
        }
    }

    private fun setFilters(filterLayout: ChapterFilterLayout) {
        presenter.setFilters(
            binding.chapterFilterLayout.showUnread.state,
            binding.chapterFilterLayout.showDownload.state,
            binding.chapterFilterLayout.showBookmark.state,
        )
        checkIfFilterMatchesDefault(filterLayout)
    }

    private fun checkIfFilterMatchesDefault(filterLayout: ChapterFilterLayout) {
        val matches = presenter.mangaFilterMatchesDefault()
        filterLayout.binding.setAsDefaultFilter.isInvisible = matches
        filterLayout.binding.resetAsDefaultFilter.isInvisible = matches
    }

    private fun checkIfSortMatchesDefault() {
        val matches = presenter.mangaSortMatchesDefault()
        binding.setAsDefaultSort.isInvisible = matches
        binding.resetAsDefaultSort.isInvisible = matches
    }

    private fun sortChanged(sortTextView: SortTextView, state: SortTextView.State) {
        if (sortTextView != binding.byChapterNumber) {
            binding.byChapterNumber.state = SortTextView.State.NONE
        }
        if (sortTextView != binding.byUploadDate) {
            binding.byUploadDate.state = SortTextView.State.NONE
        }
        if (sortTextView != binding.bySource) {
            binding.bySource.state = SortTextView.State.NONE
        }
        presenter.setSortOrder(
            when (sortTextView) {
                binding.byChapterNumber -> Manga.CHAPTER_SORTING_NUMBER
                binding.byUploadDate -> Manga.CHAPTER_SORTING_UPLOAD_DATE
                else -> Manga.CHAPTER_SORTING_SOURCE
            },
            state == SortTextView.State.DESCENDING,
        )
        checkIfSortMatchesDefault()
    }
}
