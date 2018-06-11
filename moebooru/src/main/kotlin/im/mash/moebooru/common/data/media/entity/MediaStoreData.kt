package im.mash.moebooru.common.data.media.entity

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class MediaStoreData(
        var rowId: Long,
        var uri: Uri,
        var mimeType: String,
        var dateTaken: Long,
        var dateModified: Long,
        var orientation: Int,
        var type: Type) : Parcelable {

    companion object CREATOR : Parcelable.Creator<MediaStoreData> {
        override fun createFromParcel(parcel: Parcel): MediaStoreData {
            return MediaStoreData(parcel)
        }

        override fun newArray(size: Int): Array<MediaStoreData?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readParcelable(Uri::class.java.classLoader),
            parcel.readString(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readInt(),
            Type.valueOf(parcel.readString()))

    enum class Type {
        VIDEO,
        IMAGE
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(rowId)
        parcel.writeParcelable(uri, flags)
        parcel.writeString(mimeType)
        parcel.writeLong(dateTaken)
        parcel.writeLong(dateModified)
        parcel.writeInt(orientation)
        parcel.writeString(type.name)
    }

    override fun describeContents(): Int {
        return 0
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