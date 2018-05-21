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

package im.mash.moebooru.utils

class MoeUrl {
    fun getUrl(url: String, tags: String?, limit: Int?, page: Int?): String {
        val ext = "/post.json"
        val moe = "$url$ext"
        if (tags == null && limit == null && page == null) {
            return moe
        }
        if (tags != null && limit == null && page == null) {
            return "$moe?tags=$tags"
        }
        if (tags == null && limit != null && page == null) {
            return "$moe?limit=$limit"
        }
        if (tags == null && limit == null && page != null) {
            return "$moe?page=$page"
        }
        if (tags == null && limit != null && page != null) {
            return "$moe?limit=$limit&page=$page"
        }
        if (tags != null && limit == null && page != null) {
            return "$moe?tags=$tags&page=$page"
        }
        if (tags != null && limit != null && page == null) {
            return "$moe?tags=$tags&limit=$limit"
        }
        return "$moe?tags=$tags&limit=$limit&page=$page"
    }
}