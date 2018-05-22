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

package im.mash.moebooru.models

import android.os.Parcel
import android.os.Parcelable

data class ParamGet(
        var url: String,
        var page: String?,
        var limit: String?,
        var include_votes: String?,
        var tags: String?,
        var include_tags: String?,
        var login: String?,
        var password_hash: String?
) : Parcelable {

    fun makeGetUrl(): String {
        val s1 = "$url/post.json"
        var s2 = "?page=1"
        if (page != null) s2 = "?page=$page"
        var s3 = ""
        if (limit != null) s3 = "&limit=$limit"
        var s4 = ""
        if (include_votes != null) s4 = "&include_votes=$include_votes"
        var s5 = ""
        if (tags != null) s5 = "&tags=$tags"
        var s6 = ""
        if (include_tags != null) s6 = "&include_tags=$include_tags"
        var s7 = ""
        var s8 = ""
        if (login != null && password_hash != null) {
            s7 = "&login=$login"
            s8 = "&password_hash=$password_hash"
        }
        return s1 + s2 + s3 + s4 + s5 + s6 + s7 + s8
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(limit)
        parcel.writeString(page)
        parcel.writeString(include_votes)
        parcel.writeString(tags)
        parcel.writeString(include_tags)
        parcel.writeString(login)
        parcel.writeString(password_hash)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParamGet> {
        override fun createFromParcel(parcel: Parcel): ParamGet {
            return ParamGet(parcel)
        }

        override fun newArray(size: Int): Array<ParamGet?> {
            return arrayOfNulls(size)
        }
    }
}