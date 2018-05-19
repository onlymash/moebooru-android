/*
 * Copyright (C) 2018 by onlymash <im@mash.im>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package im.mash.moebooru.glide

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders
import im.mash.moebooru.utils.Net

class GetUrl(private val url: String) {
    companion object {
        private val headers: Headers = LazyHeaders.Builder()
                .addHeader(Net.HEADER_USER_AGENT_KEY, Net.HEADER_USER_AGENT_INFO).build()
    }

    val glideUrl: GlideUrl?
        get() = GlideUrl(url, headers)
}