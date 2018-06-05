package im.mash.moebooru.content

import android.os.Environment
import android.util.Log
import im.mash.moebooru.App.Companion.app
import java.io.File

private const val TAG = "FileUtil"

val booruDir: File
    get() {
        val booru = app.boorusManager.getBooru(app.settings.activeProfile)
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Moebooru/${booru.name}")
        if (dir.exists()) {
            if (dir.isFile) {
                if (!dir.delete()) {
                    Log.i(TAG, "Exists file delete failed")
                }
                if (!dir.mkdirs()) {
                    Log.i(TAG, "Directory not created")
                }
            }
        } else {
            if (!dir.mkdirs()) {
                Log.i(TAG, "Directory not created")
            }
        }
        return dir
    }
