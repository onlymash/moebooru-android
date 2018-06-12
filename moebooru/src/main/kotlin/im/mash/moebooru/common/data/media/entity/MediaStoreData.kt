package im.mash.moebooru.common.data.media.entity

import android.net.Uri

data class MediaStoreData(
        var rowId: Long,
        var uri: Uri,
        var mimeType: String,
        var dateTaken: Long,
        var dateModified: Long,
        var orientation: Int,
        var type: Type) {

    enum class Type {
        VIDEO,
        IMAGE
    }

    override fun toString(): String {
        return ("MediaStoreData{"
                + "rowId=" + rowId
                + ", uri=" + uri
                + ", mimeType='" + mimeType + '\''.toString()
                + ", dateModified=" + dateModified
                + ", orientation=" + orientation
                + ", type=" + type
                + ", dateTaken=" + dateTaken
                + '}'.toString())
    }
}