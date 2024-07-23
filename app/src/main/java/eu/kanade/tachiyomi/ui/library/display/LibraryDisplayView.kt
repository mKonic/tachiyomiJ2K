package eu.mkonic.tachiyomi.ui.library.display

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.slider.Slider
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.databinding.LibraryDisplayLayoutBinding
import eu.mkonic.tachiyomi.util.bindToPreference
import eu.mkonic.tachiyomi.util.lang.addBetaTag
import eu.mkonic.tachiyomi.util.lang.withSubtitle
import eu.mkonic.tachiyomi.util.system.bottomCutoutInset
import eu.mkonic.tachiyomi.util.system.dpToPx
import eu.mkonic.tachiyomi.util.system.isLandscape
import eu.mkonic.tachiyomi.util.system.topCutoutInset
import eu.mkonic.tachiyomi.util.view.checkHeightThen
import eu.mkonic.tachiyomi.util.view.numberOfRowsForValue
import eu.mkonic.tachiyomi.util.view.rowsForValue
import eu.mkonic.tachiyomi.widget.BaseLibraryDisplayView
import kotlin.math.roundToInt

class LibraryDisplayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    BaseLibraryDisplayView<LibraryDisplayLayoutBinding>(context, attrs) {

    var mainView: View? = null
    override fun inflateBinding() = LibraryDisplayLayoutBinding.bind(this)
    override fun initGeneralPreferences() {
        binding.displayGroup.bindToPreference(preferences.libraryLayout())
        binding.uniformGrid.bindToPreference(preferences.uniformGrid()) {
            binding.staggeredGrid.isEnabled = !it
        }
        binding.outlineOnCovers.bindToPreference(preferences.outlineOnCovers())
        binding.staggeredGrid.text = context.getString(R.string.use_staggered_grid).addBetaTag(context)
        binding.staggeredGrid.isEnabled = !preferences.uniformGrid().get()
        binding.staggeredGrid.bindToPreference(preferences.useStaggeredGrid())
        binding.gridSeekbar.value = ((preferences.gridSize().get() + .5f) * 2f).roundToInt().toFloat()
        binding.resetGridSize.setOnClickListener {
            binding.gridSeekbar.value = 3f
        }

        binding.gridSeekbar.setLabelFormatter {
            val view = controller?.activity?.window?.decorView ?: mainView ?: this@LibraryDisplayView
            val mainText = (mainView ?: this@LibraryDisplayView).rowsForValue(it).toString()
            val mainOrientation = context.getString(
                if (context.isLandscape()) {
                    R.string.landscape
                } else {
                    R.string.portrait
                },
            )
            val alt = (
                if (view.measuredHeight >= 720.dpToPx) {
                    view.measuredHeight - 72.dpToPx
                } else {
                    view.measuredHeight
                }
                ) -
                (view.rootWindowInsets?.topCutoutInset() ?: 0) -
                (view.rootWindowInsets?.bottomCutoutInset() ?: 0)
            val altText = alt.numberOfRowsForValue(it).toString()
            val altOrientation = context.getString(
                if (context.isLandscape()) {
                    R.string.portrait
                } else {
                    R.string.landscape
                },
            )
            "$mainOrientation: $mainText • $altOrientation: $altText"
        }
        binding.gridSeekbar.addOnChangeListener { _, value, fromUser ->
            if (!fromUser) {
                preferences.gridSize().set((value / 2f) - .5f)
            }
            setGridText(value)
        }
        binding.gridSeekbar.addOnSliderTouchListener(
            object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}

                override fun onStopTrackingTouch(slider: Slider) {
                    preferences.gridSize().set((slider.value / 2f) - .5f)
                    setGridText(slider.value)
                }
            },
        )
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        checkHeightThen {
            setGridText(binding.gridSeekbar.value)
        }
    }

    private fun setGridText(progress: Float) {
        with(binding.gridSizeText) {
            val rows = (mainView ?: this@LibraryDisplayView).rowsForValue(progress)
            val titleText = context.getString(R.string.grid_size)
            val subtitleText = context.getString(R.string._per_row, rows)
            text = titleText.withSubtitle(context, subtitleText)
        }
    }
}
