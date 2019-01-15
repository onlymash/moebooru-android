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

import im.mash.moebooru.common.data.local.entity.Comment
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import okhttp3.HttpUrl
import java.net.URI

class CommentRepository(private val local: CommentDataContract.Local,
                        private val remote: CommentDataContract.Remote,
                        private val scheduler: Scheduler) : CommentDataContract.Repository {

    private val compositeDisposable = CompositeDisposable()

    override val commentOutcome: PublishSubject<Outcome<MutableList<Comment>>>
        = PublishSubject.create<Outcome<MutableList<Comment>>>()

    override fun loadComment(host: String, commentId: Int) {
        commentOutcome.loading(true)
        local.loadComment(host, commentId)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ comments ->
                    commentOutcome.success(comments)
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun loadComments(host: String, postId: Int) {
        commentOutcome.loading(true)
        local.loadComments(host, postId)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ comments ->
                    commentOutcome.success(comments)
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun loadComments(host: String) {
        commentOutcome.loading(true)
        local.loadComments(host)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ comments ->
                    commentOutcome.success(comments)
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun refreshComments(url: HttpUrl) {
        remote.getComments(url)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ comments ->
                    val host = url.host()
                    comments.forEach { comment ->
                        comment.host = host
                    }
                    local.saveComments(comments)
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun createComment(url: String, postId: Int, body: String, anonymous: Int, username: String, passwordHash: String) {
        remote.createComment(url, postId, body, anonymous, username, passwordHash)
                .performOnBackOutOnMain(scheduler)
                .subscribe()
    }

    override fun deleteComment(url: String, commentId: Int, username: String, passwordHash: String) {
        remote.destroyComment(url, commentId, username, passwordHash)
                .performOnBackOutOnMain(scheduler)
                .subscribe({
                    val host = URI(url).host
                    local.deleteComment(host, commentId)
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun handleError(error: Throwable) {
        commentOutcome.failed(error)
    }
}