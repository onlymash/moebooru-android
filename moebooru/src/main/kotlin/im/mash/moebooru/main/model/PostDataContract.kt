package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import okhttp3.HttpUrl

interface PostDataContract {
    interface Repository {
        val postFetchOutcome: PublishSubject<Outcome<MutableList<Post>>>
        fun isNotMore(): Boolean
        fun fetchPosts(httpUrl: HttpUrl)
        fun refreshPosts(httpUrl: HttpUrl)
        fun loadMorePosts(httpUrl: HttpUrl)
        fun addPosts(posts: MutableList<Post>)
        fun deletePosts(site: String)
        fun handleError(error: Throwable)
    }
    interface Local {
        fun getPosts(site: String): Flowable<MutableList<Post>>
        fun addPosts(posts: MutableList<Post>)
        fun deletePosts(site: String)
    }
    interface Remote {
        fun getPosts(httpUrl: HttpUrl): Single<MutableList<Post>>
    }
}