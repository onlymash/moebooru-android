package im.mash.moebooru.main.model


import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.core.extensions.performOnBack
import im.mash.moebooru.core.network.Scheduler
import io.reactivex.Completable
import io.reactivex.Flowable

class PostLocalData(private val database: MoeDatabase,
                    private val scheduler: Scheduler)
    : PostDataContract.Local {

    override fun getPosts(site: String): Flowable<MutableList<Post>> {
        return database.postDao().getPosts(site)
    }

    override fun addPosts(posts: MutableList<Post>) {
        Completable.fromAction{
            database.postDao().insertPosts(posts)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun savePosts(site: String, posts: MutableList<Post>) {
        Completable.fromAction{
            database.postDao().deletePosts(site)
            database.postDao().insertPosts(posts)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun deletePosts(site: String) {
        Completable.fromAction{
            database.postDao().deletePosts(site)
        }
                .performOnBack(scheduler)
                .subscribe()
    }
}