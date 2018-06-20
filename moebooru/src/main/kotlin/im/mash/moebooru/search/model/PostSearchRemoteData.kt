package im.mash.moebooru.search.model

import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.common.data.remote.PostSearchService
import io.reactivex.Single
import okhttp3.HttpUrl

class PostSearchRemoteData(private val postSearchService: PostSearchService) : PostSearchDataContract.Remote {

    override fun getPosts(httpUrl: HttpUrl): Single<MutableList<PostSearch>> {
        return postSearchService.getPosts(httpUrl)
    }
}