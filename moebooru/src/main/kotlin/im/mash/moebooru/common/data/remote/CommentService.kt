package im.mash.moebooru.common.data.remote

import im.mash.moebooru.common.data.local.entity.Comment
import im.mash.moebooru.common.data.remote.entity.CommentResponse
import io.reactivex.Single
import okhttp3.HttpUrl
import retrofit2.http.*

interface CommentService {

    /* comment/search.json?query=user:username
     * comment.json?post_id=post_id
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
                      @Field("password_hash") passwordHash: String): Single<CommentResponse>

    /* comment/destroy.json
     */
    @FormUrlEncoded
    @HTTP(method = "DELETE", hasBody = true)
    fun destroyComment(@Url url: String,
                       @Field("id") commentId: Int,
                       @Field("login") username: String,
                       @Field("password_hash") passwordHash: String): Single<CommentResponse>
}