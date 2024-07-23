package eu.mkonic.tachiyomi.ui.extension

import android.animation.AnimatorInflater
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.scale
import androidx.core.view.isGone
import androidx.core.view.isVisible
import coil.dispose
import coil.load
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.data.image.coil.CoverViewTarget
import eu.mkonic.tachiyomi.databinding.ExtensionCardItemBinding
import eu.mkonic.tachiyomi.extension.model.Extension
import eu.mkonic.tachiyomi.extension.model.InstallStep
import eu.mkonic.tachiyomi.extension.model.InstalledExtensionsOrder
import eu.mkonic.tachiyomi.extension.util.ExtensionLoader
import eu.mkonic.tachiyomi.ui.base.holder.BaseFlexibleViewHolder
import eu.mkonic.tachiyomi.util.system.LocaleHelper
import eu.mkonic.tachiyomi.util.system.getResourceColor
import eu.mkonic.tachiyomi.util.system.timeSpanFromNow
import eu.mkonic.tachiyomi.util.view.resetStrokeColor
import java.util.Locale

class ExtensionHolder(view: View, val adapter: ExtensionAdapter) :
    BaseFlexibleViewHolder(view, adapter) {

    private val binding = ExtensionCardItemBinding.bind(view)
    init {
        binding.extButton.setOnClickListener {
            adapter.buttonClickListener.onButtonClick(flexibleAdapterPosition)
        }
        binding.cancelButton.setOnClickListener {
            adapter.buttonClickListener.onCancelClick(flexibleAdapterPosition)
        }
    }

    fun bind(item: ExtensionItem) {
        val extension = item.extension

        // Set source name

        val infoText = mutableListOf(extension.versionName)
        binding.date.isVisible = false
        if (extension is Extension.Installed && !extension.hasUpdate) {
            when (InstalledExtensionsOrder.fromValue(adapter.installedSortOrder)) {
                InstalledExtensionsOrder.RecentlyUpdated -> {
                    ExtensionLoader.extensionUpdateDate(itemView.context, extension)
                        .takeUnless { it == 0L }?.let {
                            binding.date.isVisible = true
                            binding.date.text = itemView.context.timeSpanFromNow(R.string.updated_, it)
                            infoText.add("")
                        }
                }
                InstalledExtensionsOrder.RecentlyInstalled -> {
                    ExtensionLoader.extensionInstallDate(itemView.context, extension)
                        .takeUnless { it == 0L }?.let {
                            binding.date.isVisible = true
                            binding.date.text =
                                itemView.context.timeSpanFromNow(
                                    if (extension.isShared) {
                                        R.string.installed_
                                    } else {
                                        R.string.added_
                                    },
                                    it,
                                )
                            infoText.add("")
                        }
                }
                else -> binding.date.isVisible = false
            }
        } else {
            binding.date.isVisible = false
        }
        binding.lang.isVisible = binding.date.isGone
        binding.extTitle.text = if (infoText.size > 1) {
            buildSpannedString {
                append(extension.name + " ")
                color(binding.extTitle.context.getResourceColor(android.R.attr.textColorSecondary)) {
                    scale(0.75f) {
                        append(LocaleHelper.getDisplayName(extension.lang))
                    }
                }
            }
        } else {
            extension.name
        }

        binding.version.text = infoText.joinToString(" • ")
        binding.lang.text = LocaleHelper.getDisplayName(extension.lang)
        binding.warning.text = when {
            extension.isNsfw -> itemView.context.getString(R.string.nsfw_short)
            else -> ""
        }.uppercase(Locale.ROOT)
        binding.installProgress.progress = item.sessionProgress ?: 0
        binding.installProgress.isVisible = item.sessionProgress != null
        binding.cancelButton.isVisible = item.sessionProgress != null

        binding.sourceImage.dispose()

        if (extension is Extension.Available) {
            binding.sourceImage.load(extension.iconUrl) {
                target(CoverViewTarget(binding.sourceImage))
            }
        } else if (extension is Extension.Installed) {
            binding.sourceImage.load(extension.icon)
        }
        bindButton(item)
    }

    @Suppress("ResourceType")
    fun bindButton(item: ExtensionItem) = with(binding.extButton) {
        if (item.installStep == InstallStep.Done) return@with
        isEnabled = true
        isClickable = true
        isActivated = false

        binding.installProgress.progress = item.sessionProgress ?: 0
        binding.cancelButton.isVisible = item.sessionProgress != null
        binding.installProgress.isVisible = item.sessionProgress != null
        val extension = item.extension
        val installStep = item.installStep
        strokeColor = ColorStateList.valueOf(Color.TRANSPARENT)
        rippleColor = ColorStateList.valueOf(context.getResourceColor(R.attr.colorControlHighlight))
        stateListAnimator = null
        if (installStep != null) {
            setText(
                when (installStep) {
                    InstallStep.Pending -> R.string.pending
                    InstallStep.Downloading -> R.string.downloading
                    InstallStep.Loading -> R.string.loading
                    InstallStep.Installing -> R.string.installing
                    InstallStep.Installed -> R.string.installed
                    InstallStep.Error -> R.string.retry
                    else -> return@with
                },
            )
            if (installStep != InstallStep.Error) {
                isEnabled = false
                isClickable = false
            }
        } else if (extension is Extension.Installed) {
            when {
                extension.hasUpdate -> {
                    isActivated = true
                    stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.icon_btn_state_list_anim)
                    rippleColor = ColorStateList.valueOf(context.getColor(R.color.on_secondary_highlight))
                    setText(R.string.update)
                }
                else -> {
                    setText(R.string.settings)
                }
            }
        } else if (extension is Extension.Untrusted) {
            resetStrokeColor()
            setText(R.string.trust)
        } else {
            resetStrokeColor()
            setText(if (adapter.installPrivately) R.string.add else R.string.install)
        }
    }
}
