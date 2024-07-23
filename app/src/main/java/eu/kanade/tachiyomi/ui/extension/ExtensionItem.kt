package eu.mkonic.tachiyomi.ui.extension

import android.content.pm.PackageInstaller
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.extension.model.Extension
import eu.mkonic.tachiyomi.extension.model.InstallStep
import eu.mkonic.tachiyomi.source.CatalogueSource

/**
 * Item that contains source information.
 *
 * @param source Instance of [CatalogueSource] containing source information.
 * @param header The header for this item.
 */
data class ExtensionItem(
    val extension: Extension,
    val header: ExtensionGroupItem? = null,
    val installStep: InstallStep? = null,
    val session: PackageInstaller.SessionInfo? = null,
) :
    AbstractSectionableItem<ExtensionHolder, ExtensionGroupItem>(header) {

    constructor(
        extension: Extension,
        header: ExtensionGroupItem? = null,
        installInfo: ExtensionIntallInfo?,
    ) : this(extension, header, installInfo?.first, installInfo?.second)

    val sessionProgress: Int?
        get() = (session?.progress?.times(100)?.toInt())

    /**
     * Returns the layout resource of this item.
     */
    override fun getLayoutRes(): Int {
        return R.layout.extension_card_item
    }

    /**
     * Creates a new view holder for this item.
     */
    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ExtensionHolder {
        return ExtensionHolder(view, adapter as ExtensionAdapter)
    }

    /**
     * Binds this item to the given view holder.
     */
    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: ExtensionHolder,
        position: Int,
        payloads: MutableList<Any?>?,
    ) {
        if (payloads.isNullOrEmpty()) {
            holder.bind(this)
        } else {
            holder.bindButton(this)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return extension.pkgName == (other as ExtensionItem).extension.pkgName
    }

    override fun hashCode(): Int {
        return extension.pkgName.hashCode()
    }
}
