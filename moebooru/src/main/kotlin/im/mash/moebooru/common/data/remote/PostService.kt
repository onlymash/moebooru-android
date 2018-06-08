package im.mash.moebooru.common.data.remote

import im.mash.moebooru.common.data.local.entity.Post
import io.reactivex.Flowable
import okhttp3.HttpUrl
import retrofit2.http.GET
import retrofit2.http.Url

interface PostService {
    @GET
    fun getPosts(@Url url: HttpUrl): Flowable<MutableList<Post>>
}