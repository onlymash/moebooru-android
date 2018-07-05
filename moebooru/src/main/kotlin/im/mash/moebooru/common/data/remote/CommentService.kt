package im.mash.moebooru.common.data.remote

import im.mash.moebooru.common.data.local.entity.Comment
import io.reactivex.Single
import okhttp3.HttpUrl
import retrofit2.http.*

interface CommentService {

    /* comment/search.json?query=
     */
    @GET
    fun getComments(@Url url: HttpUrl): Single<MutableList<Comment>>

    /* comment/create.json
     */
    @POST
    @FormUrlEncoded
    fun createComment(@Url url: String,
                      @Field("comment[post_id]") postId: Int,
                      @Field("comment[body]") body: String,
                      @Field("comment[anonymous]") anonymous: Int,
                      @Field("login") username: String,
                      @Field("password_hash") passwordHash: String): Single<String>

    /* comment/destroy.json
     */
    @DELETE
    @FormUrlEncoded
    fun destroyComment(@Url url: String,
                       @Field("comment_id") commentId: Int,
                       @Field("login") username: String,
                       @Field("password_hash") passwordHash: String): Single<String>
}