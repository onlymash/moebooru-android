package im.mash.moebooru.search.model

import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.util.logi
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import okhttp3.HttpUrl

class PostSearchRepository(private val local: PostSearchDataContract.Local,
                 private val remote: PostSearchDataContract.Remote,
                 private val scheduler: Scheduler,
                 private val compositeDisposable: CompositeDisposable) : PostSearchDataContract.Repository {

    companion object {
        private const val TAG = "PostSearchRepository"
    }

    private var notMore = false
    private var deleting = false
    override fun isNotMore(): Boolean = notMore

    override val postFetchOutcome: PublishSubject<Outcome<MutableList<PostSearch>>>
            = PublishSubject.create<Outcome<MutableList<PostSearch>>>()

    override fun fetchPosts(httpUrl: HttpUrl) {
        postFetchOutcome.loading(true)
        app.settings.isSearchLoading = true
        var tags = httpUrl.queryParameter("tags")
        if (tags == null) tags = ""
        //Observe changes to the db
        local.getPosts(httpUrl.host(), tags)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ posts ->
                    if (!deleting) {
                        postFetchOutcome.success(posts)
                        app.settings.isSearchLoading = false
                    }
                    deleting = false
                }, {
                    error ->
                    app.settings.isSearchLoading = false
                    handleError(error)
                })
                .addTo(compositeDisposable)
    }

    override fun refreshPosts(httpUrl: HttpUrl) {
        app.settings.isSearchLoading = true
        var tags = httpUrl.queryParameter("tags")
        if (tags == null) tags = ""
        notMore = false
        remote.getPosts(httpUrl)
                .performOnBackOutOnMain(scheduler)
                .subscribe(
                        { posts ->
                            posts.forEach { post ->
                                post.site = httpUrl.host()
                                post.keyword = tags
                            }
                            savePosts(httpUrl.host(), posts, tags)
                            app.settings.isSearchLoading = false
                        },
                        { error ->
                            app.settings.isSearchLoading = false
                            handleError(error)
                        })
                .addTo(compositeDisposable)
    }

    override fun loadMorePosts(httpUrl: HttpUrl) {
        if (notMore) {
            return
        }
        app.settings.isSearchLoading = true
        var tags = httpUrl.queryParameter("tags")
        if (tags == null) tags = ""
        remote.getPosts(httpUrl)
                .performOnBackOutOnMain(scheduler)
                .subscribe(
                        { posts ->
                            val limit = httpUrl.queryParameter("limit")!!.toInt()
                            val size = posts.size
                            logi(TAG, "loadMorePosts. size: $size")
                            if (size > 0) {
                                posts.forEach { post ->
                                    post.site = httpUrl.host()
                                    post.keyword = tags
                                }
                                addPosts(posts)
                            }
                            if (size < limit) {
                                notMore = true
                            }
                            app.settings.isSearchLoading = false
                        },
                        { error ->
                            app.settings.isSearchLoading = false
                            handleError(error)
                        })
                .addTo(compositeDisposable)
    }

    private fun savePosts(site: String, posts: MutableList<PostSearch>, tags: String) {
        deleting = true
        Completable.fromAction{
            deletePosts(site, tags)
        }
                .performOnBack(scheduler)
                .subscribe({
                    addPosts(posts)
                }, { error ->
                    deleting = false
                    handleError(error)
                })
    }

    override fun deletePosts(site: String, tags: String) {
        local.deletePosts(site, tags)
    }

    override fun addPosts(posts: MutableList<PostSearch>) {
        local.addPosts(posts)
    }

    override fun handleError(error: Throwable) {
        postFetchOutcome.failed(error)
    }
}