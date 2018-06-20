package im.mash.moebooru.main.model

import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.util.logi
import io.reactivex.Completable
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

    private var notMore = false
    private var deleting = false
    override fun isNotMore(): Boolean = notMore

    override val postFetchOutcome: PublishSubject<Outcome<MutableList<Post>>> = PublishSubject.create<Outcome<MutableList<Post>>>()

    override fun fetchPosts(httpUrl: HttpUrl) {
        postFetchOutcome.loading(true)
        if (!app.settings.isLoading) app.settings.isLoading = true
        //Observe changes to the db
        local.getPosts(httpUrl.host())
                .performOnBackOutOnMain(scheduler)
                .subscribe({ posts ->
                    if (!deleting) {
                        postFetchOutcome.success(posts)
                        app.settings.isLoading = false
                    }
                    deleting = false
                }, { error ->
                    app.settings.isLoading = false
                    handleError(error)
                })
                .addTo(compositeDisposable)
    }

    override fun refreshPosts(httpUrl: HttpUrl) {
        notMore = false
        if (!app.settings.isLoading) app.settings.isLoading = true
        remote.getPosts(httpUrl)
                .performOnBackOutOnMain(scheduler)
                .subscribe(
                        { posts ->
                            posts.forEach { post ->
                                post.site = httpUrl.host()
                            }
                            savePosts(httpUrl.host(), posts)
                            app.settings.isLoading = false
                        },
                        { error ->
                            app.settings.isLoading = false
                            handleError(error)
                        })
                .addTo(compositeDisposable)
    }

    override fun loadMorePosts(httpUrl: HttpUrl) {
        if (notMore) return
        if (!app.settings.isLoading) app.settings.isLoading = true
        remote.getPosts(httpUrl)
                .performOnBackOutOnMain(scheduler)
                .subscribe(
                        { posts ->
                            val limit = httpUrl.queryParameter("limit")!!.toInt()
                            val size = posts.size
                            if (size > 0) {
                                posts.forEach { post ->
                                    post.site = httpUrl.host()
                                }
                                addPosts(posts)
                            }
                            if (size < limit) {
                                notMore = true
                            }
                            app.settings.isLoading = false
                        },
                        { error ->
                            app.settings.isLoading = false
                            handleError(error)
                        })
                .addTo(compositeDisposable)
    }

    private fun savePosts(site: String, posts: MutableList<Post>) {
        deleting = true
        Completable.fromAction{
            deletePosts(site)
        }
                .performOnBack(scheduler)
                .subscribe({
                    addPosts(posts)
                }, { error ->
                    deleting = false
                    handleError(error)
                })
    }

    override fun deletePosts(site: String) {
        local.deletePosts(site)
    }

    override fun addPosts(posts: MutableList<Post>) {
        local.addPosts(posts)
    }

    override fun handleError(error: Throwable) {
        postFetchOutcome.failed(error)
    }
}