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

package im.mash.moebooru.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import im.mash.moebooru.common.data.local.entity.Comment
import im.mash.moebooru.common.model.CommentDataContract
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.disposables.CompositeDisposable
import okhttp3.HttpUrl

class CommentViewModel(private val repo: CommentDataContract.Repository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val commentOutcome: LiveData<Outcome<MutableList<Comment>>> by lazy {
        repo.commentOutcome.toLiveData(compositeDisposable)
    }

    fun loadComment(host: String, commentId: Int) {
        repo.loadComment(host, commentId)
    }
    fun loadComments(host: String, postId: Int) {
        repo.loadComments(host, postId)
    }
    fun loadComments(host: String) {
        repo.loadComments(host)
    }
    fun refreshComments(url: HttpUrl) {
        repo.refreshComments(url)
    }
    fun createComment(url: String, postId: Int, body: String, anonymous: Int, username: String, passwordHash: String) {
        repo.createComment(url, postId, body, anonymous, username, passwordHash)
    }
    fun deleteComment(url: String, commentId: Int, username: String, passwordHash: String) {
        repo.deleteComment(url, commentId, username, passwordHash)
    }
}