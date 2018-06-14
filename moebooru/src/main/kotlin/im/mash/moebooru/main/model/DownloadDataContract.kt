package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.local.entity.PostDownload
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.subjects.PublishSubject

interface DownloadDataContract {
    interface Repository {
        val downloadPostsOutcome: PublishSubject<Outcome<MutableList<PostDownload>>>
        fun loadPosts()
        fun addPost(post: PostDownload)
        fun deletePost(post: PostDownload)
        fun deletePosts(posts: MutableList<PostDownload>)
        fun deleteAll()
        fun handleError(error: Throwable)
    }
}