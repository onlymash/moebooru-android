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

package im.mash.moebooru.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object MailUtil {

    fun mailFile(context: Context, mailAddress: String, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822"
        val receiver: Array<String> = arrayOf(mailAddress)
        intent.putExtra(Intent.EXTRA_EMAIL, receiver)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Moebooru crash report")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.putExtra(Intent.EXTRA_TEXT, "What happened")
        try {
            context.startActivity(Intent.createChooser(intent, "Choose email client"))
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

}
