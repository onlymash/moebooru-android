package im.mash.moebooru.common.data.remote

import im.mash.moebooru.common.data.remote.entity.RawUser
import io.reactivex.Single
import okhttp3.HttpUrl
import retrofit2.http.GET
import retrofit2.http.Url

interface UserService {
    @GET
    fun getUser(@Url url: HttpUrl): Single<MutableList<RawUser>>
}