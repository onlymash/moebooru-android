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

import im.mash.moebooru.common.data.local.entity.PostSearch
import io.reactivex.Single
import okhttp3.HttpUrl
import retrofit2.http.GET
import retrofit2.http.Url

interface PostSearchService {
    @GET
    fun getPosts(@Url url: HttpUrl): Single<MutableList<PostSearch>>
}