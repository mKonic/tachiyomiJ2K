package eu.mkonic.tachiyomi.extension.model

sealed interface LoadResult {

    data class Success(val extension: Extension.Installed) : LoadResult
    data class Untrusted(val extension: Extension.Untrusted) : LoadResult
    data object Error : LoadResult
}
