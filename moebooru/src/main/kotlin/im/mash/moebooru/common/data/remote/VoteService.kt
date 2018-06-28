package im.mash.moebooru.common.data.remote

import im.mash.moebooru.common.data.remote.entity.Vote
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

interface VoteService {

    @FormUrlEncoded
    @POST
    fun votePost(@Url url: String,
                 @Field("id") id: Int,
                 @Field("score") score: Int,
                 @Field("login") username: String,
                 @Field("password_hash") passwordHash: String): Single<Vote>
}
