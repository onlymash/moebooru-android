/*
 * Copyright (C) 2018 by onlymash <im@mash.im>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package im.mash.moebooru.database

import android.util.Log
import im.mash.moebooru.models.RawPost
import im.mash.moebooru.utils.PostsTable
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

class DatabasePostsManager(private val database: DatabaseHelper) {

    companion object {
        private var instance: DatabasePostsManager? = null

        @Synchronized
        fun getInstance(database: DatabaseHelper): DatabasePostsManager {
            if (instance == null) {
                instance = DatabasePostsManager(database)
            }
            return instance!!
        }

    }

    fun savePosts(posts: MutableList<RawPost>, site: Long) {
        posts.forEach {
            database.use {
                insert(PostsTable.TABLE_NAME,
                PostsTable.SITE to site,
                PostsTable.ID to it.id,
                PostsTable.TAGS to it.tags,
                PostsTable.CREATE_AT to it.created_at,
                PostsTable.CREATOR_ID to it.creator_id,
                PostsTable.AUTHOR to it.author,
                PostsTable.CHANGE to it.change,
                PostsTable.SOURCE to it.source,
                PostsTable.SCORE to it.score,
                PostsTable.MD5 to it.md5,
                PostsTable.FILE_SIZE to it.file_size,
                PostsTable.FILE_URL to it.file_url,
                PostsTable.IS_SHOWN_IN_INDEX to it.is_shown_in_index,
                PostsTable.PREVIEW_URL to it.preview_url,
                PostsTable.PREVIEW_WIDTH to it.preview_width,
                PostsTable.PREVIEW_HEIGHT to it.preview_height,
                PostsTable.ACTUAL_PREVIEW_WIDTH to it.actual_preview_width,
                PostsTable.ACTUAL_PREVIEW_HEIGHT to it.actual_preview_height,
                PostsTable.SAMPLE_URL to it.sample_url,
                PostsTable.SAMPLE_WIDTH to it.sample_width,
                PostsTable.SAMPLE_HEIGHT to it.sample_height,
                PostsTable.SAMPLE_FILE_SIZE to it.sample_file_size,
                PostsTable.JPEG_URL to it.jpeg_url,
                PostsTable.JPEG_WIDTH to it.jpeg_width,
                PostsTable.JPEG_HEIGHT to it.jpeg_height,
                PostsTable.JPEG_FILE_SIZE to it.jpeg_file_size,
                PostsTable.RATING to it.rating,
                PostsTable.HAS_CHILDRE to it.has_children,
                PostsTable.PARENT_ID to it.parent_id,
                PostsTable.STATUS to it.status,
                PostsTable.WIDTH to it.width,
                PostsTable.HEIGHT to it.height,
                PostsTable.IS_HELD to it.is_held)
            }
        }
    }

    fun getPost(site: Long): RawPost? {
        val post: RawPost? = null
        database.use {
            select(PostsTable.TABLE_NAME)
                    .whereSimple("${PostsTable.SITE} = $site")
        }
        return post
    }

    fun getPostFromId(site: Long, id: Int): RawPost? {
        var post: RawPost? = null
        if (id > -1) {
            database.use {
                select(PostsTable.TABLE_NAME)
                        .whereSimple("${PostsTable.SITE} = $site and ${PostsTable.ID} = $id")
                        .parseOpt(object : MapRowParser<RawPost> {
                            override fun parseRow(columns: Map<String, Any?>): RawPost {
                                post = makePost(columns)
                                return post!!
                            }
                        })
            }
        }
        return post
    }

    fun loadPosts(site: Long): MutableList<RawPost> {
        val posts: MutableList<RawPost> = mutableListOf()
        Log.i(this.javaClass.simpleName, "site: $site")
        database.use {
            select(PostsTable.TABLE_NAME)
                    .whereSimple("${PostsTable.SITE} = $site")
                    .parseList(object : MapRowParser<MutableList<RawPost>> {
                        override fun parseRow(columns: Map<String, Any?>): MutableList<RawPost> {
                            posts.add(makePost(columns))
                            return posts
                        }
                    })
        }
        return posts
    }

    fun deletePosts(site: Long) {
        database.use {
            delete(PostsTable.TABLE_NAME, "${PostsTable.SITE} = $site")
        }
    }

    private fun makePost(columns: Map<String, Any?>): RawPost {
        val id: Long? = columns.getValue(PostsTable.ID) as? Long
        val tags: String? = columns.getValue(PostsTable.TAGS) as? String
        val createdAt: Long? = columns.getValue(PostsTable.CREATE_AT) as? Long
        val creatorId: Long? = columns.getValue(PostsTable.CREATOR_ID) as? Long
        val author: String? = columns.getValue(PostsTable.AUTHOR) as? String
        val change: Long? = columns.getValue(PostsTable.CHANGE) as? Long
        val source: String? = columns.getValue(PostsTable.SOURCE) as? String
        val score: Long? = columns.getValue(PostsTable.SCORE) as? Long
        val md5: String? = columns.getValue(PostsTable.MD5) as? String
        val fileSize: Long? = columns.getValue(PostsTable.FILE_SIZE) as? Long
        val fileUrl: String? = columns.getValue(PostsTable.FILE_URL) as? String
        var isShownInIndex: Boolean? = false
        if (columns.getValue(PostsTable.IS_SHOWN_IN_INDEX) as? Long == 1L) { isShownInIndex = true }
        val previewUrl: String? = columns.getValue(PostsTable.PREVIEW_URL) as? String
        val previewWidth: Long? = columns.getValue(PostsTable.PREVIEW_WIDTH) as? Long
        val previewHeight: Long? = columns.getValue(PostsTable.PREVIEW_HEIGHT) as? Long
        val actualPreviewWidth: Long? = columns.getValue(PostsTable.ACTUAL_PREVIEW_WIDTH) as? Long
        val actualPreviewHeight: Long? = columns.getValue(PostsTable.ACTUAL_PREVIEW_HEIGHT) as? Long
        val sampleUrl: String? = columns.getValue(PostsTable.SAMPLE_URL) as? String
        val sampleWidth: Long? = columns.getValue(PostsTable.SAMPLE_WIDTH) as? Long
        val sampleHeight: Long? = columns.getValue(PostsTable.SAMPLE_HEIGHT) as? Long
        val sampleFileSize: Long? = columns.getValue(PostsTable.SAMPLE_FILE_SIZE) as? Long
        val jpegUrl: String? = columns.getValue(PostsTable.JPEG_URL) as? String
        val jpegWidth: Long? = columns.getValue(PostsTable.JPEG_WIDTH) as? Long
        val jpegHeight: Long? = columns.getValue(PostsTable.JPEG_HEIGHT) as? Long
        val jpegFileSize: Long? = columns.getValue(PostsTable.JPEG_FILE_SIZE) as? Long
        val rating: String? = columns.getValue(PostsTable.RATING) as? String
        var hasChildren: Boolean? = false
        if (columns.getValue(PostsTable.HAS_CHILDRE) as? Long == 1L) { hasChildren = true }
        val parentId: Long? = columns.getValue(PostsTable.PARENT_ID) as? Long
        val status: String? = columns.getValue(PostsTable.STATUS) as? String
        val width: Long? = columns.getValue(PostsTable.WIDTH) as? Long
        val height: Long? = columns.getValue(PostsTable.HEIGHT) as? Long
        var isHeld: Boolean? = false
        if (columns.getValue(PostsTable.IS_HELD) as? Long == 1L) { isHeld = true }
        return RawPost(id, tags, createdAt, creatorId, author, change, source, score,
                md5, fileSize, fileUrl, isShownInIndex, previewUrl, previewWidth, previewHeight,
                actualPreviewWidth, actualPreviewHeight, sampleUrl, sampleWidth, sampleHeight, sampleFileSize,
                jpegUrl, jpegWidth, jpegHeight, jpegFileSize, rating, hasChildren, parentId, status,
                width, height, isHeld)
    }
}