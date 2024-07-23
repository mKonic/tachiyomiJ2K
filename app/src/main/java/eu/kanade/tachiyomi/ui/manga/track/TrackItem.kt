package eu.mkonic.tachiyomi.ui.manga.track

import eu.mkonic.tachiyomi.data.database.models.Track
import eu.mkonic.tachiyomi.data.track.TrackService

data class TrackItem(val track: Track?, val service: TrackService)
