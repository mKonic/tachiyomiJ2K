package eu.mkonic.tachiyomi.data.image.coil

import coil.key.Keyer
import coil.request.Options
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.util.storage.DiskUtil

class MangaCoverKeyer : Keyer<Manga> {
    override fun key(data: Manga, options: Options): String? {
        if (data.thumbnail_url.isNullOrBlank()) return null
        return if (!data.favorite) {
            data.thumbnail_url!!
        } else {
            DiskUtil.hashKeyForDisk(data.thumbnail_url!!)
        }
    }
}
