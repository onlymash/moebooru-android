/*
 * Copyright (C) 2019 by onlymash <im@fiepi.me>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

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