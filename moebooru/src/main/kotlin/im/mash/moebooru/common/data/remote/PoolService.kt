package im.mash.moebooru.common.data.remote

import im.mash.moebooru.common.data.local.entity.Pool
import io.reactivex.Single
import okhttp3.HttpUrl
import retrofit2.http.GET
import retrofit2.http.Url

interface PoolService {
    @GET
    fun getPools(@Url url: HttpUrl): Single<MutableList<Pool>>
}