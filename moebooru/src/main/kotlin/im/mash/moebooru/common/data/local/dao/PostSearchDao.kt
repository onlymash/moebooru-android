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

package im.mash.moebooru.common.data.local.dao

import androidx.room.*
import im.mash.moebooru.common.data.local.entity.PostSearch
import io.reactivex.Flowable

@Dao
interface PostSearchDao {

    @Query("SELECT * FROM posts_search ORDER BY id DESC")
    fun getAll(): Flowable<MutableList<PostSearch>>

    @Query("SELECT * FROM posts_search WHERE site=:site AND keyword = :tags AND id = :id")
    fun getPost(site: String, tags: String, id: Int): Flowable<PostSearch>

    @Query("SELECT * FROM posts_search WHERE site=:site ORDER BY id DESC")
    fun getPosts(site: String): Flowable<MutableList<PostSearch>>

    @Query("SELECT * FROM posts_search WHERE site=:site AND keyword = :tags ORDER BY id DESC")
    fun getPosts(site: String, tags: String): Flowable<MutableList<PostSearch>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPost(postSearch: PostSearch)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPosts(posts: MutableList<PostSearch>)

    @Query("DELETE FROM posts_search WHERE site = :site AND keyword = :keyword")
    fun deletePosts(site: String, keyword: String)

    @Query("DELETE FROM posts_search WHERE site = :site AND keyword = :keyword AND id NOT IN (SELECT id FROM posts_search WHERE site = :site AND keyword = :keyword ORDER BY id DESC LIMIT :limit)")
    fun deletePosts(site: String, keyword: String, limit: Int)

    @Query("DELETE FROM posts_search WHERE site = :site AND keyword = :keyword AND id = :id")
    fun deletePost(site: String, keyword: String, id: Int)

    @Delete
    fun delete(postSearch: PostSearch)

    @Delete
    fun delete(postsSearch: MutableList<PostSearch>)

    @Query("SELECT * FROM posts_search AS a WHERE site=:site AND keyword = :tags AND id = (SELECT MAX(b.id) FROM posts_search AS b WHERE a.site = b.site AND a.keyword = b.keyword)")
    fun getLastPost(site: String, tags: String): Flowable<PostSearch>

    @Query("SELECT id FROM posts_search WHERE site = :site AND keyword = :keyword ORDER BY id DESC")
    fun getPostsId(site: String, keyword: String): Flowable<MutableList<Int>>

    @Query("SELECT id FROM posts_search WHERE site = :site AND keyword IN (:keyword1, :keyword2) ORDER BY id DESC")
    fun getPostsId(site: String, keyword1: String, keyword2: String): Flowable<MutableList<Int>>
}