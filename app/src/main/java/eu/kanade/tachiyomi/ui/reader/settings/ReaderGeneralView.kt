package eu.mkonic.tachiyomi.ui.reader.settings

import android.content.Context
import android.util.AttributeSet
import eu.mkonic.tachiyomi.databinding.ReaderGeneralLayoutBinding
import eu.mkonic.tachiyomi.ui.reader.ReaderActivity
import eu.mkonic.tachiyomi.util.bindToPreference
import eu.mkonic.tachiyomi.widget.BaseReaderSettingsView

class ReaderGeneralView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    BaseReaderSettingsView<ReaderGeneralLayoutBinding>(context, attrs) {

    lateinit var sheet: TabbedReaderSettingsSheet
    override fun inflateBinding() = ReaderGeneralLayoutBinding.bind(this)
    override fun initGeneralPreferences() {
        binding.viewerSeries.onItemSelectedListener = { position ->
            val readingModeType = ReadingModeType.fromSpinner(position)
            (context as ReaderActivity).viewModel.setMangaReadingMode(readingModeType.flagValue)

            val mangaViewer = activity.viewModel.getMangaReadingMode()
            if (mangaViewer == ReadingModeType.WEBTOON.flagValue || mangaViewer == ReadingModeType.CONTINUOUS_VERTICAL.flagValue) {
                initWebtoonPreferences()
            } else {
                initPagerPreferences()
            }
        }
        binding.viewerSeries.setSelection(
            (context as? ReaderActivity)?.viewModel?.state?.value?.manga?.readingModeType?.let {
                ReadingModeType.fromPreference(it).prefValue
            } ?: 0,
        )
        binding.rotationMode.onItemSelectedListener = { position ->
            val rotationType = OrientationType.fromSpinner(position)
            (context as ReaderActivity).viewModel.setMangaOrientationType(rotationType.flagValue)
        }
        binding.rotationMode.setSelection(
            (context as ReaderActivity).viewModel.manga?.orientationType?.let {
                OrientationType.fromPreference(it).prefValue
            } ?: 0,
        )

        binding.backgroundColor.setEntries(
            ReaderBackgroundColor.entries
                .map { context.getString(it.stringRes) },
        )
        val selection = ReaderBackgroundColor.indexFromPref(preferences.readerTheme().get())
        binding.backgroundColor.setSelection(selection)
        binding.backgroundColor.onItemSelectedListener = { position ->
            val backgroundColor = ReaderBackgroundColor.entries[position]
            preferences.readerTheme().set(backgroundColor.prefValue)
        }
        binding.showPageNumber.bindToPreference(preferences.showPageNumber())
        binding.fullscreen.bindToPreference(preferences.fullscreen())
        binding.keepscreen.bindToPreference(preferences.keepScreenOn())
        binding.alwaysShowChapterTransition.bindToPreference(preferences.alwaysShowChapterTransition())
    }

    /**
     * Init the preferences for the webtoon reader.
     */
    private fun initWebtoonPreferences() {
        sheet.updateTabs(true)
    }

    private fun initPagerPreferences() {
        sheet.updateTabs(false)
    }
}
