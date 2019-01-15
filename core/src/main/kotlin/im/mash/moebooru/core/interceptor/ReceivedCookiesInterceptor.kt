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

package im.mash.moebooru.core.interceptor

import im.mash.moebooru.core.constants.Cookies
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

/* credit: https://gist.github.com/tsuharesu/cbfd8f02d46498b01f1b */
class ReceivedCookiesInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            val cookies = HashSet<String>()

            for (header in originalResponse.headers("Set-Cookie")) {
                println("Set-Cookie: $header")
                cookies.add(header)
            }
            Cookies.values = cookies
        }
        return originalResponse
    }
}