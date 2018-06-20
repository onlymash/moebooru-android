package im.mash.moebooru.search.model


import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.extensions.performOnBack
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.util.logi
import io.reactivex.Completable
import io.reactivex.Flowable

class PostSearchLocalData(private val database: MoeDatabase,
                    private val scheduler: Scheduler)
    : PostSearchDataContract.Local {

    companion object {
        private const val TAG = "PostSearchLocalData"
    }

    override fun getPosts(site: String, tags: String): Flowable<MutableList<PostSearch>> {
        return database.postSearchDao().getPosts(site, tags)
    }

    override fun addPosts(posts: MutableList<PostSearch>) {
        Completable.fromAction{
            database.postSearchDao().insertPosts(posts)
        }
                .performOnBack(scheduler)
                .subscribe({}, {error -> logi(TAG, error.message.toString())})
    }

    override fun deletePosts(site: String, tags: String) {
        database.postSearchDao().deletePosts(site, tags)
    }
}