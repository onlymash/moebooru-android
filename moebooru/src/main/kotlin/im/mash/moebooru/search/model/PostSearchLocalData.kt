package im.mash.moebooru.search.model


import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.extensions.performOnBack
import im.mash.moebooru.core.scheduler.Scheduler
import io.reactivex.Completable
import io.reactivex.Flowable

class PostSearchLocalData(private val database: MoeDatabase,
                    private val scheduler: Scheduler)
    : PostSearchDataContract.Local {

    override fun getPosts(site: String, tags: String): Flowable<MutableList<PostSearch>> {
        return database.postSearchDao().getPosts(site, tags)
    }

    override fun addPosts(posts: MutableList<PostSearch>) {
        Completable.fromAction{
            database.postSearchDao().insertPosts(posts)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun savePosts(site: String, tags: String, posts: MutableList<PostSearch>) {
        Completable.fromAction{
            database.postSearchDao().deletePosts(site, tags)
            database.postSearchDao().insertPosts(posts)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun deletePosts(site: String, tags: String) {
        Completable.fromAction{
            database.postSearchDao().deletePosts(site, tags)
        }
                .performOnBack(scheduler)
                .subscribe()
    }
}