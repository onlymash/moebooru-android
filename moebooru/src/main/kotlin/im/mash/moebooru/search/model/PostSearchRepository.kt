package im.mash.moebooru.search.model

import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.util.logi
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

    //Need to perform a remoteFetch or not?
    private var remoteFetch = true

    private var notMore = false

    override fun isNotMore(): Boolean = notMore

    override val postFetchOutcome: PublishSubject<Outcome<MutableList<PostSearch>>>
            = PublishSubject.create<Outcome<MutableList<PostSearch>>>()

    override fun fetchPosts(httpUrl: HttpUrl) {
        postFetchOutcome.loading(true)
        var tags = httpUrl.queryParameter("tags")
        if (tags == null) tags = ""
        logi(TAG, "remoteFetch: $remoteFetch")
        //Observe changes to the db
        local.getPosts(httpUrl.host(), tags)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ posts ->
                    postFetchOutcome.success(posts)
                    if ((posts == null || posts.size == 0) && remoteFetch) {
                        refreshPosts(httpUrl)
                        remoteFetch = false
                    }
                    logi(TAG, "fetchPosts. post size: ${posts.size}")
                }, {
                    error -> handleError(error) })
                .addTo(compositeDisposable)
    }

    override fun refreshPosts(httpUrl: HttpUrl) {
        logi(TAG, "refreshPosts")
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
                            local.deletePosts(httpUrl.host(), tags)
                            addPosts(posts)
                        },
                        { error -> handleError(error) })
                .addTo(compositeDisposable)
    }

    override fun loadMorePosts(httpUrl: HttpUrl) {
        if (notMore) {
            return
        }
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
                        },
                        { error -> handleError(error) })
                .addTo(compositeDisposable)
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