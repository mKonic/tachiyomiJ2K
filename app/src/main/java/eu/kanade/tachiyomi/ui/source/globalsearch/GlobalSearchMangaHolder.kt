package eu.mkonic.tachiyomi.ui.source.globalsearch

import android.graphics.drawable.RippleDrawable
import android.view.View
import androidx.core.view.isVisible
import coil.Coil
import coil.dispose
import coil.request.CachePolicy
import coil.request.ImageRequest
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.data.image.coil.CoverViewTarget
import eu.mkonic.tachiyomi.data.image.coil.MangaCoverFetcher
import eu.mkonic.tachiyomi.databinding.SourceGlobalSearchControllerCardItemBinding
import eu.mkonic.tachiyomi.ui.base.holder.BaseFlexibleViewHolder
import eu.mkonic.tachiyomi.util.system.dpToPx
import eu.mkonic.tachiyomi.util.view.makeShapeCorners
import eu.mkonic.tachiyomi.util.view.setCards

class GlobalSearchMangaHolder(view: View, adapter: GlobalSearchCardAdapter) :
    BaseFlexibleViewHolder(view, adapter) {

    private val binding = SourceGlobalSearchControllerCardItemBinding.bind(view)
    init {
        itemView.setOnClickListener {
            val item = adapter.getItem(flexibleAdapterPosition)
            if (item != null) {
                adapter.mangaClickListener.onMangaClick(item.manga)
            }
        }
        val bottom = 2.dpToPx
        val others = 5.dpToPx
        (binding.constraintLayout.foreground as? RippleDrawable)?.apply {
            setLayerSize(1, 0, 0)
            for (i in 0 until numberOfLayers) {
                setLayerInset(i, others, others, others, bottom)
            }
        }
        binding.favoriteButton.shapeAppearanceModel =
            binding.card.makeShapeCorners(binding.card.radius, binding.card.radius)
        itemView.setOnLongClickListener {
            adapter.mangaClickListener.onMangaLongClick(flexibleAdapterPosition, adapter)
            true
        }
        setCards(adapter.showOutlines, binding.card, binding.favoriteButton)
    }

    fun bind(manga: Manga) {
        binding.title.text = manga.title
        binding.favoriteButton.isVisible = manga.favorite
        setImage(manga)
    }

    fun setImage(manga: Manga) {
        binding.itemImage.dispose()
        if (!manga.thumbnail_url.isNullOrEmpty()) {
            val request = ImageRequest.Builder(itemView.context).data(manga)
                .placeholder(android.R.color.transparent)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .target(CoverViewTarget(binding.itemImage, binding.progress))
                .setParameter(MangaCoverFetcher.useCustomCover, false)
                .build()
            Coil.imageLoader(itemView.context).enqueue(request)
        }
    }
}
