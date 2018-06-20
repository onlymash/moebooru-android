package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.remote.PostService
import io.reactivex.Single
import okhttp3.HttpUrl

class PostRemoteData(private val postService: PostService) : PostDataContract.Remote {

    override fun getPosts(httpUrl: HttpUrl): Single<MutableList<Post>> {
        return postService.getPosts(httpUrl)
    }
}