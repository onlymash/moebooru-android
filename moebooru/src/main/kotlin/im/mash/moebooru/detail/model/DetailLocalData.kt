package im.mash.moebooru.detail.model

import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.scheduler.Scheduler
import io.reactivex.Flowable

class DetailLocalData(private val database: MoeDatabase,
                      private val scheduler: Scheduler) : DetailDataContract.Local {

    override fun getPosts(site: String): Flowable<MutableList<Post>> {
        return database.postDao().getPosts(site)
    }

    override fun getPosts(site: String, tags: String): Flowable<MutableList<PostSearch>> {
        return database.postSearchDao().getPosts(site, tags)
    }
}