package im.mash.moebooru.search.model

import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import okhttp3.HttpUrl

interface PostSearchDataContract {
    interface Repository {
        val postFetchOutcome: PublishSubject<Outcome<MutableList<PostSearch>>>
        val isEndOutCome: PublishSubject<Outcome<Boolean>>
        fun isNotMore(): Boolean
        fun fetchPosts(httpUrl: HttpUrl)
        fun refreshPosts(httpUrl: HttpUrl)
        fun loadMorePosts(httpUrl: HttpUrl)
        fun addPosts(posts: MutableList<PostSearch>)
        fun deletePosts(site: String, tags: String, limit: Int)
        fun handleError(error: Throwable)
    }
    interface Local {
        fun getPosts(site: String, tags: String): Flowable<MutableList<PostSearch>>
        fun addPosts(posts: MutableList<PostSearch>)
        fun deletePosts(site: String, tags: String, limit: Int)
    }
    interface Remote {
        fun getPosts(httpUrl: HttpUrl): Single<MutableList<PostSearch>>
    }
}