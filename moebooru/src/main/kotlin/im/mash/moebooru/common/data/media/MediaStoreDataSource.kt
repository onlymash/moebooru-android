/*
 * Copyright (C) 2019 by onlymash <im@fiepi.me>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package im.mash.moebooru.common.data.media

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import im.mash.moebooru.common.data.media.entity.MediaStoreData
import io.reactivex.Flowable

class MediaStoreDataSource(private val context: Context) {

    companion object {
        private const val TAG = "MediaStoreDataSource"

        private val IMAGE_PROJECTION = arrayOf(
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATE_MODIFIED,
                MediaStore.Images.ImageColumns.MIME_TYPE,
                MediaStore.Images.ImageColumns.ORIENTATION,
                MediaStore.Images.Media.DATA)

        private val VIDEO_PROJECTION = arrayOf(
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATE_TAKEN,
                MediaStore.Video.VideoColumns.DATE_MODIFIED,
                MediaStore.Video.VideoColumns.MIME_TYPE,
                "0 AS " + MediaStore.Images.ImageColumns.ORIENTATION,
                MediaStore.Video.Media.DATA)
    }

    fun loadMediaData(path: String): Flowable<MutableList<MediaStoreData>> {
        val data = queryImages(path)
        data.addAll(queryVideos(path))
        data.sortWith(Comparator { mediaStoreData, mediaStoreData2 ->
            when {
                mediaStoreData.dateTaken < mediaStoreData2.dateTaken -> 1
                mediaStoreData.dateTaken > mediaStoreData2.dateTaken -> -1
                else -> 0
            }
        })
        return Flowable.just(data)
    }

    private fun queryImages(path: String): MutableList<MediaStoreData> {
        val selection = MediaStore.Images.Media.DATA + " like ?"
        val selectionArgs = arrayOf("$path%")
        return query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATE_MODIFIED,
                MediaStore.Images.ImageColumns.MIME_TYPE, MediaStore.Images.ImageColumns.ORIENTATION,
                MediaStore.Images.Media.DATA, MediaStoreData.Type.IMAGE, selection, selectionArgs)
    }

    private fun queryVideos(path: String): MutableList<MediaStoreData> {
        val selection = MediaStore.Video.Media.DATA + " like ?"
        val selectionArgs = arrayOf("$path%")
        return query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION,
                MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DATE_MODIFIED,
                MediaStore.Video.VideoColumns.MIME_TYPE, MediaStore.Images.ImageColumns.ORIENTATION,
                MediaStore.Video.Media.DATA, MediaStoreData.Type.VIDEO, selection, selectionArgs)
    }

    private fun query(contentUri: Uri, projection: Array<String>, sortByCol: String, idCol: String, dateTakenCol: String,
                      dateModifiedCol: String, mimeTypeCol: String, orientationCol: String, mediaDataCol: String,
                      type: MediaStoreData.Type, selection: String, selectionArgs: Array<String>): MutableList<MediaStoreData> {

        val data: MutableList<MediaStoreData> = mutableListOf()

        val cr = context.contentResolver

        val cursor = cr.query(contentUri, projection, selection, selectionArgs, "$sortByCol DESC")

        cursor?.use { c ->
            val idColNum = c.getColumnIndexOrThrow(idCol)
            val dateTakenColNum = c.getColumnIndexOrThrow(dateTakenCol)
            val dateModifiedColNum = c.getColumnIndexOrThrow(dateModifiedCol)
            val mimeTypeColNum = c.getColumnIndex(mimeTypeCol)
            val orientationColNum = c.getColumnIndexOrThrow(orientationCol)
            val mediaDataNum = c.getColumnIndexOrThrow(mediaDataCol)

            while (c.moveToNext()) {
                val id = c.getLong(idColNum)
                val dateTaken = c.getLong(dateTakenColNum)
                val mimeType = c.getString(mimeTypeColNum)
                val dateModified = c.getLong(dateModifiedColNum)
                val orientation = c.getInt(orientationColNum)
                val mediaData = c.getString(mediaDataNum)

                data.add(MediaStoreData(id, Uri.withAppendedPath(contentUri, id.toString()),
                        mimeType, dateTaken, dateModified, orientation, type, mediaData))
            }
        }

        return data
    }
}