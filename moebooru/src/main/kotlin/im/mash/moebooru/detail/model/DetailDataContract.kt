package im.mash.moebooru.detail.model

import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.network.Outcome
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject

interface DetailDataContract {
    interface Repository {
        val postFetchOutcome: PublishSubject<Outcome<MutableList<Post>>>
        val postSearchFetchOutcome: PublishSubject<Outcome<MutableList<PostSearch>>>
        fun fetchPosts(site: String)
        fun fetchPosts(site: String, tags: String)
        fun handleError(error: Throwable)
        fun handleSearchError(error: Throwable)
    }
    interface Local {
        fun getPosts(site: String): Flowable<MutableList<Post>>
        fun getPosts(site: String, tags: String): Flowable<MutableList<PostSearch>>
    }
}