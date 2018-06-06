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

import im.mash.moebooru.model.RawPost
import im.mash.moebooru.utils.PostsTable
import im.mash.moebooru.utils.SearchTable
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

class PostsSearchManager(private val database: DatabaseHelper) {

    private val TAG = this.javaClass.simpleName

    companion object {
        private var instance: PostsSearchManager? = null

        @Synchronized
        fun getInstance(database: DatabaseHelper): PostsSearchManager {
            if (instance == null) {
                instance = PostsSearchManager(database)
            }
            return instance!!
        }

    }

    fun savePosts(posts: MutableList<RawPost>, site: Long, tagsKey: String) {
        posts.forEach {
            database.use {
                insert(SearchTable.TABLE_NAME,
                        SearchTable.KEY_WORD to tagsKey,
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

    fun getPost(site: Long, tagsKey: String): RawPost? {
        val post: RawPost? = null
        database.use {
            select(SearchTable.TABLE_NAME)
                    .whereSimple("(${PostsTable.SITE} = ?) and (${SearchTable.KEY_WORD} = ?)", site.toString(), tagsKey)
        }
        return post
    }

    fun getPostFromId(site: Long, id: Int): RawPost? {
        var post: RawPost? = null
        if (id > -1) {
            database.use {
                select(SearchTable.TABLE_NAME)
                        .whereSimple("(${PostsTable.SITE} = ?) and (${PostsTable.ID} = ?)", site.toString(), id.toString())
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

    fun loadPosts(site: Long, tagsKey: String): MutableList<RawPost>? {
        val posts: MutableList<RawPost> = mutableListOf()
        try {
            database.use {
                select(SearchTable.TABLE_NAME)
                        .whereSimple("(${PostsTable.SITE} = ?) and (${SearchTable.KEY_WORD} = ?)", site.toString(), tagsKey)
                        .parseList(object : MapRowParser<MutableList<RawPost>> {
                            override fun parseRow(columns: Map<String, Any?>): MutableList<RawPost> {
                                posts.add(makePost(columns))
                                return posts
                            }
                        })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (posts.size > 0) { posts } else { null }
    }

    fun deletePosts(site: Long, tagsKey: String) {
        database.use {
            execSQL("delete from ${SearchTable.TABLE_NAME} where ${PostsTable.SITE} = $site and ${SearchTable.KEY_WORD} = '$tagsKey'")
        }
    }
}