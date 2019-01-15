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
import im.mash.moebooru.common.data.local.entity.Comment
import io.reactivex.Flowable
import retrofit2.http.DELETE

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE host = :host ORDER BY id DESC")
    fun getComments(host: String): Flowable<MutableList<Comment>>

    @Query("SELECT * FROM comments WHERE host = :host AND post_id = :postId ORDER BY id DESC")
    fun getCommentsByPostId(host: String, postId: Int): Flowable<MutableList<Comment>>

    @Query("SELECT * FROM comments WHERE host = :host AND id = :commentId ORDER BY id DESC")
    fun getCommentByCommentId(host: String, commentId: Int): Flowable<MutableList<Comment>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertComment(comment: Comment)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertComments(comment: MutableList<Comment>)

    @Delete
    fun deleteComment(comment: Comment)

    @Query("DELETE FROM comments WHERE host = :host AND id = :commentId")
    fun deleteCommentById(host: String, commentId: Int)

    @Delete
    fun deleteComments(comment: MutableList<Comment>)
}