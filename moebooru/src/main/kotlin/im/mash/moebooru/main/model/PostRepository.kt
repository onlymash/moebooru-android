package im.mash.moebooru.main.model

import android.util.Log
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.network.Outcome
import im.mash.moebooru.core.network.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import okhttp3.HttpUrl

class PostRepository(private val local: PostDataContract.Local,
                 private val remote: PostDataContract.Remote,
                 private val scheduler: Scheduler,
                 private val compositeDisposable: CompositeDisposable) : PostDataContract.Repository {

    companion object {
        private const val TAG = "PostRepository"
    }

    //Need to perform a remoteFetch or not?
    var remoteFetch = true

    override val postFetchOutcome: PublishSubject<Outcome<MutableList<Post>>> = PublishSubject.create<Outcome<MutableList<Post>>>()

    override fun fetchPosts(httpUrl: HttpUrl) {
        postFetchOutcome.loading(true)
        //Observe changes to the db
        local.getPosts(httpUrl.host())
                .performOnBackOutOnMain(scheduler)
                .subscribe({ posts ->
                    postFetchOutcome.success(posts)
                    if ((posts == null || posts.size == 0) && remoteFetch) {
                        refreshPosts(httpUrl)
                        remoteFetch = false
                    }
                    Log.i(TAG, "fetchPosts. post size: ${posts.size}")
                }, {
                    error -> handleError(error) })
                .addTo(compositeDisposable)
    }

    override fun refreshPosts(httpUrl: HttpUrl) {
        postFetchOutcome.loading(true)
        remote.getPosts(httpUrl)
                .performOnBackOutOnMain(scheduler)
                .subscribe(
                        { posts ->
                            posts.forEach { post ->
                                post.site = httpUrl.host()
                            }
                            local.savePosts(httpUrl.host(), posts)
                        },
                        { error -> handleError(error) })
    }

    override fun loadMorePosts(httpUrl: HttpUrl) {
        postFetchOutcome.loading(true)
        remote.getPosts(httpUrl)
        remote.getPosts(httpUrl)
                .performOnBackOutOnMain(scheduler)
                .subscribe(
                        { posts ->
                            posts.forEach { post ->
                                post.site = httpUrl.host()
                            }
                            local.addPosts(posts)
                        },
                        { error -> handleError(error) })
    }

    override fun handleError(error: Throwable) {
        postFetchOutcome.failed(error)
    }

}