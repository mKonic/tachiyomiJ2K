package eu.mkonic.tachiyomi.data.database.mappers

import android.content.ContentValues
import android.database.Cursor
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery
import com.pushtorefresh.storio.sqlite.queries.InsertQuery
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.data.database.models.MangaImpl
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_ARTIST
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_AUTHOR
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_CHAPTER_FLAGS
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_DATE_ADDED
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_DESCRIPTION
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_FAVORITE
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_FILTERED_SCANLATORS
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_GENRE
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_HIDE_TITLE
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_ID
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_INITIALIZED
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_LAST_UPDATE
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_SOURCE
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_STATUS
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_THUMBNAIL_URL
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_TITLE
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_UPDATE_STRATEGY
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_URL
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.COL_VIEWER
import eu.mkonic.tachiyomi.data.database.tables.MangaTable.TABLE
import eu.mkonic.tachiyomi.data.database.updateStrategyAdapter

class MangaTypeMapping : SQLiteTypeMapping<Manga>(
    MangaPutResolver(),
    MangaGetResolver(),
    MangaDeleteResolver(),
)

class MangaPutResolver : DefaultPutResolver<Manga>() {

    override fun mapToInsertQuery(obj: Manga) = InsertQuery.builder()
        .table(TABLE)
        .build()

    override fun mapToUpdateQuery(obj: Manga) = UpdateQuery.builder()
        .table(TABLE)
        .where("$COL_ID = ?")
        .whereArgs(obj.id)
        .build()

    override fun mapToContentValues(obj: Manga) = ContentValues(15).apply {
        put(COL_ID, obj.id)
        put(COL_SOURCE, obj.source)
        put(COL_URL, obj.url)
        put(COL_ARTIST, obj.originalArtist)
        put(COL_AUTHOR, obj.originalAuthor)
        put(COL_DESCRIPTION, obj.originalDescription)
        put(COL_GENRE, obj.originalGenre)
        put(COL_TITLE, obj.originalTitle)
        put(COL_STATUS, obj.originalStatus)
        put(COL_THUMBNAIL_URL, obj.thumbnail_url)
        put(COL_FAVORITE, obj.favorite)
        put(COL_LAST_UPDATE, obj.last_update)
        put(COL_INITIALIZED, obj.initialized)
        put(COL_VIEWER, obj.viewer_flags)
        put(COL_HIDE_TITLE, obj.hide_title)
        put(COL_CHAPTER_FLAGS, obj.chapter_flags)
        put(COL_DATE_ADDED, obj.date_added)
        put(COL_FILTERED_SCANLATORS, obj.filtered_scanlators)
        put(COL_UPDATE_STRATEGY, obj.update_strategy.let(updateStrategyAdapter::encode))
    }
}

interface BaseMangaGetResolver {
    fun mapBaseFromCursor(manga: Manga, cursor: Cursor) = manga.apply {
        id = cursor.getLong(cursor.getColumnIndex(COL_ID))
        source = cursor.getLong(cursor.getColumnIndex(COL_SOURCE))
        url = cursor.getString(cursor.getColumnIndex(COL_URL))
        artist = cursor.getString(cursor.getColumnIndex(COL_ARTIST))
        author = cursor.getString(cursor.getColumnIndex(COL_AUTHOR))
        description = cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION))
        genre = cursor.getString(cursor.getColumnIndex(COL_GENRE))
        title = cursor.getString(cursor.getColumnIndex(COL_TITLE))
        status = cursor.getInt(cursor.getColumnIndex(COL_STATUS))
        thumbnail_url = cursor.getString(cursor.getColumnIndex(COL_THUMBNAIL_URL))
        favorite = cursor.getInt(cursor.getColumnIndex(COL_FAVORITE)) == 1
        last_update = cursor.getLong(cursor.getColumnIndex(COL_LAST_UPDATE))
        initialized = cursor.getInt(cursor.getColumnIndex(COL_INITIALIZED)) == 1
        viewer_flags = cursor.getInt(cursor.getColumnIndex(COL_VIEWER))
        chapter_flags = cursor.getInt(cursor.getColumnIndex(COL_CHAPTER_FLAGS))
        hide_title = cursor.getInt(cursor.getColumnIndex(COL_HIDE_TITLE)) == 1
        date_added = cursor.getLong(cursor.getColumnIndex(COL_DATE_ADDED))
        filtered_scanlators = cursor.getString(cursor.getColumnIndex(COL_FILTERED_SCANLATORS))
        update_strategy = cursor.getInt(cursor.getColumnIndex(COL_UPDATE_STRATEGY)).let(
            updateStrategyAdapter::decode,
        )
    }
}

open class MangaGetResolver : DefaultGetResolver<Manga>(), BaseMangaGetResolver {

    override fun mapFromCursor(cursor: Cursor): Manga {
        return mapBaseFromCursor(MangaImpl(), cursor)
    }
}

class MangaDeleteResolver : DefaultDeleteResolver<Manga>() {

    override fun mapToDeleteQuery(obj: Manga) = DeleteQuery.builder()
        .table(TABLE)
        .where("$COL_ID = ?")
        .whereArgs(obj.id)
        .build()
}
