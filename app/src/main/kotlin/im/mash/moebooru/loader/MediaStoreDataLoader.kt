package im.mash.moebooru.loader

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.AsyncTaskLoader
import im.mash.moebooru.model.MediaStoreData
import kotlin.Comparator


/**
 * Loads metadata from the media store for images and videos.
 */
class MediaStoreDataLoader internal constructor(context: Context, private val path: String) : AsyncTaskLoader<MutableList<MediaStoreData>>(context) {

    companion object {
        private val IMAGE_PROJECTION = arrayOf(
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATE_MODIFIED,
                MediaStore.Images.ImageColumns.MIME_TYPE,
                MediaStore.Images.ImageColumns.ORIENTATION)

        private val VIDEO_PROJECTION = arrayOf(
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATE_TAKEN,
                MediaStore.Video.VideoColumns.DATE_MODIFIED,
                MediaStore.Video.VideoColumns.MIME_TYPE,
                "0 AS " + MediaStore.Images.ImageColumns.ORIENTATION)
    }

    private var cached: MutableList<MediaStoreData>? = null
    private var observerRegistered = false
    private val forceLoadContentObserver = ForceLoadContentObserver()

    override fun deliverResult(data: MutableList<MediaStoreData>?) {
        if (!isReset && isStarted) {
            super.deliverResult(data)
        }
    }

    override fun onStartLoading() {
        if (cached != null) {
            deliverResult(cached)
        }
        if (takeContentChanged() || cached == null) {
            forceLoad()
        }
        registerContentObserver()
    }

    override fun onStopLoading() {
        cancelLoad()
    }

    override fun onReset() {
        super.onReset()

        onStopLoading()
        cached = null
        unregisterContentObserver()
    }

    override fun onAbandon() {
        super.onAbandon()
        unregisterContentObserver()
    }

    override fun loadInBackground(): MutableList<MediaStoreData>? {
        val data = queryImages()
        data.addAll(queryVideos())
        data.sortWith(Comparator<MediaStoreData> { mediaStoreData, mediaStoreData2 ->
            when {
                mediaStoreData.dateTaken < mediaStoreData2.dateTaken -> 1
                mediaStoreData.dateTaken > mediaStoreData2.dateTaken -> -1
                else -> 0
            }
        })
        return data
    }

    private fun queryImages(): MutableList<MediaStoreData> {
        val selection = MediaStore.Images.Media.DATA + " like ?"
        val selectionArgs = arrayOf("$path%")
        return query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATE_MODIFIED,
                MediaStore.Images.ImageColumns.MIME_TYPE, MediaStore.Images.ImageColumns.ORIENTATION,
                MediaStoreData.Type.IMAGE, selection, selectionArgs)
    }

    private fun queryVideos(): MutableList<MediaStoreData> {
        val selection = MediaStore.Video.Media.DATA + " like ?"
        val selectionArgs = arrayOf("$path%")
        return query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION,
                MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DATE_MODIFIED,
                MediaStore.Video.VideoColumns.MIME_TYPE, MediaStore.Images.ImageColumns.ORIENTATION,
                MediaStoreData.Type.VIDEO, selection, selectionArgs)
    }

    private fun query(contentUri: Uri, projection: Array<String>, sortByCol: String,
                      idCol: String, dateTakenCol: String, dateModifiedCol: String, mimeTypeCol: String,
                      orientationCol: String, type: MediaStoreData.Type, selection: String, selectionArgs: Array<String>): MutableList<MediaStoreData> {
        val data: MutableList<MediaStoreData> = mutableListOf()
        val cursor = context.contentResolver
                .query(contentUri, projection, selection, selectionArgs, "$sortByCol DESC") ?: return data

        cursor.use { c ->
            val idColNum = c.getColumnIndexOrThrow(idCol)
            val dateTakenColNum = c.getColumnIndexOrThrow(dateTakenCol)
            val dateModifiedColNum = c.getColumnIndexOrThrow(dateModifiedCol)
            val mimeTypeColNum = c.getColumnIndex(mimeTypeCol)
            val orientationColNum = c.getColumnIndexOrThrow(orientationCol)

            while (c.moveToNext()) {
                val id = c.getLong(idColNum)
                val dateTaken = c.getLong(dateTakenColNum)
                val mimeType = c.getString(mimeTypeColNum)
                val dateModified = c.getLong(dateModifiedColNum)
                val orientation = c.getInt(orientationColNum)

                data.add(MediaStoreData(id, Uri.withAppendedPath(contentUri, id.toString()),
                        mimeType, dateTaken, dateModified, orientation, type))
            }
        }

        return data
    }

    private fun registerContentObserver() {
        if (!observerRegistered) {
            val cr = context.contentResolver
            cr.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false,
                    forceLoadContentObserver)
            cr.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, false,
                    forceLoadContentObserver)

            observerRegistered = true
        }
    }

    private fun unregisterContentObserver() {
        if (observerRegistered) {
            observerRegistered = false

            context.contentResolver.unregisterContentObserver(forceLoadContentObserver)
        }
    }
}

