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
