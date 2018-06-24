package im.mash.moebooru.common.data.remote

import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

interface VoteService {

    @FormUrlEncoded
    @POST
    fun votePost(@Url url: String,
                 @Field("id") id: String,
                 @Field("score") score: String,
                 @Field("login") name: String,
                 @Field("password_hash") passwordHash: String): Single<String>
}
