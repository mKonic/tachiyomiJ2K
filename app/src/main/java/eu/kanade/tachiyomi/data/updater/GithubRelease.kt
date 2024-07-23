package eu.mkonic.tachiyomi.data.updater

import android.os.Build
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Release object.
 * Contains information about the latest release from GitHub.
 *
 * @param version version of latest release.
 * @param info log of latest release.
 * @param assets assets of latest release.
 */
@Serializable
data class GithubRelease(
    @SerialName("tag_name") val version: String,
    @SerialName("body") val info: String,
    @SerialName("html_url") val releaseLink: String,
    @SerialName("prerelease") val preRelease: Boolean?,
    @SerialName("assets") private val assets: List<Assets>,
) {

    /**
     * Get download link of latest release from the assets.
     * @return download link of latest release.
     */
    val downloadLink: String
        get() {
            val apkVariant = when (Build.SUPPORTED_ABIS[0]) {
                "arm64-v8a" -> "-arm64-v8a"
                "armeabi-v7a" -> "-armeabi-v7a"
                "x86" -> "-x86"
                "x86_64" -> "-x86_64"
                else -> ""
            }

            return assets.find { it.downloadLink.contains("tachiyomij2k$apkVariant-") }?.downloadLink
                ?: assets[0].downloadLink
        }

    /**
     * Assets class containing download url.
     * @param downloadLink download url.
     */
    @Serializable
    data class Assets(@SerialName("browser_download_url") val downloadLink: String)
}
