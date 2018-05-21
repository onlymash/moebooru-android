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

import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.models.RawPost
import im.mash.moebooru.utils.PostsTable
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

class DatabasePostsManager(private var database: DatabaseHelper) {

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

    fun savePosts(posts: MutableList<RawPost>) {
        val site = app.settings.activeProfile
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

    fun loadPosts(): MutableList<RawPost> {
        val site = app.settings.activeProfile
        val posts = mutableListOf<RawPost>()
        database.use {
            select(PostsTable.TABLE_NAME)
                    .whereSimple("${PostsTable.SITE} = ?", site.toString())
                    .parseOpt(object : MapRowParser<MutableList<RawPost>> {
                        override fun parseRow(columns: Map<String, Any?>): MutableList<RawPost> {
                            val id: Long = columns.getValue(PostsTable.ID) as Long
                            val tags: String = columns.getValue(PostsTable.TAGS) as String
                            val created_at: Long = columns.getValue(PostsTable.CREATE_AT) as Long
                            val creator_id: Long = columns.getValue(PostsTable.CREATOR_ID) as Long
                            val author: String = columns.getValue(PostsTable.AUTHOR) as String
                            val change: Long = columns.getValue(PostsTable.CHANGE) as Long
                            val source: String = columns.getValue(PostsTable.SOURCE) as String
                            val score: Int = columns.getValue(PostsTable.SCORE) as Int
                            val md5: String = columns.getValue(PostsTable.MD5) as String
                            val file_size: Int = columns.getValue(PostsTable.FILE_SIZE) as Int
                            val file_url: String = columns.getValue(PostsTable.FILE_URL) as String
                            val is_shown_in_index: Boolean = columns.getValue(PostsTable.IS_SHOWN_IN_INDEX) as Boolean
                            val preview_url: String = columns.getValue(PostsTable.PREVIEW_URL) as String
                            val preview_width: Int = columns.getValue(PostsTable.PREVIEW_WIDTH) as Int
                            val preview_height: Int = columns.getValue(PostsTable.PREVIEW_HEIGHT) as Int
                            val actual_preview_width: Int = columns.getValue(PostsTable.ACTUAL_PREVIEW_WIDTH) as Int
                            val actual_preview_height: Int = columns.getValue(PostsTable.ACTUAL_PREVIEW_HEIGHT) as Int
                            val sample_url: String = columns.getValue(PostsTable.SAMPLE_URL) as String
                            val sample_width: Int = columns.getValue(PostsTable.SAMPLE_WIDTH) as Int
                            val sample_height: Int = columns.getValue(PostsTable.SAMPLE_HEIGHT) as Int
                            val sample_file_size: Int = columns.getValue(PostsTable.SAMPLE_FILE_SIZE) as Int
                            val jpeg_url: String = columns.getValue(PostsTable.JPEG_URL) as String
                            val jpeg_width: Int = columns.getValue(PostsTable.JPEG_WIDTH) as Int
                            val jpeg_height: Int = columns.getValue(PostsTable.JPEG_HEIGHT) as Int
                            val jpeg_file_size: Int = columns.getValue(PostsTable.JPEG_FILE_SIZE) as Int
                            val rating: String = columns.getValue(PostsTable.RATING) as String
                            val has_children: Boolean = columns.getValue(PostsTable.HAS_CHILDRE) as Boolean
                            val parent_id: Long = columns.getValue(PostsTable.PARENT_ID) as Long
                            val status: String = columns.getValue(PostsTable.STATUS) as String
                            val width: Int = columns.getValue(PostsTable.WIDTH) as Int
                            val height: Int = columns.getValue(PostsTable.HEIGHT) as Int
                            val is_held: Boolean = columns.getValue(PostsTable.IS_HELD) as Boolean
                            val post = RawPost(id, tags, created_at, creator_id, author, change, source, score, md5, file_size, file_url, is_shown_in_index, preview_url, preview_width, preview_height, actual_preview_width, actual_preview_height, sample_url, sample_width, sample_height, sample_file_size, jpeg_url, jpeg_width, jpeg_height, jpeg_file_size, rating, has_children, parent_id, status, width, height, is_held)
                            posts.add(post)
                            return posts
                        }
                    })
        }
        return posts
    }
}