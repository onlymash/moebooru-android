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

package im.mash.moebooru.common.model

import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.local.entity.Comment
import im.mash.moebooru.core.extensions.performOnBack
import im.mash.moebooru.core.scheduler.Scheduler
import io.reactivex.Completable
import io.reactivex.Flowable

class CommentLocalData(private val database: MoeDatabase,
                       private val scheduler: Scheduler) : CommentDataContract.Local {

    override fun loadComment(host: String, commentId: Int): Flowable<MutableList<Comment>> {
        return database.commentDao().getCommentByCommentId(host, commentId)
    }

    override fun loadComments(host: String, postId: Int): Flowable<MutableList<Comment>> {
        return database.commentDao().getCommentsByPostId(host, postId)
    }

    override fun loadComments(host: String): Flowable<MutableList<Comment>> {
        return database.commentDao().getComments(host)
    }

    override fun saveComment(comment: Comment) {
        Completable.fromAction { database.commentDao().insertComment(comment) }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun saveComments(comments: MutableList<Comment>) {
        Completable.fromAction { database.commentDao().insertComments(comments) }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun deleteComment(host: String, commentId: Int) {
        Completable.fromAction { database.commentDao().deleteCommentById(host, commentId) }
                .performOnBack(scheduler)
                .subscribe()
    }
}