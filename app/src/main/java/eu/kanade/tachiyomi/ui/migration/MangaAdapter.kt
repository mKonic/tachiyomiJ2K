package eu.mkonic.tachiyomi.ui.migration

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible

class MangaAdapter(listener: Any, val showOutline: Boolean) :
    FlexibleAdapter<IFlexible<*>>(null, listener) {

    private var items: List<IFlexible<*>>? = null

    override fun updateDataSet(items: MutableList<IFlexible<*>>?) {
        if (this.items !== items) {
            this.items = items
            super.updateDataSet(items)
        }
    }
}
