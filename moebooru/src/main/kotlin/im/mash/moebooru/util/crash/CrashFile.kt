package im.mash.moebooru.util.crash

import android.content.Context
import java.io.File
import java.io.FileFilter

class CrashFile {
    fun getLogFile(context: Context): Array<File> {
        try {
            val path = File(context.getExternalFilesDir("logs")?.absolutePath)
            if (path.exists() && path.isDirectory) {
                val logs = path.listFiles(CrashLogFilter())
                if (logs != null && logs.isNotEmpty()) {
                    return logs
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arrayOf()
    }

    inner class CrashLogFilter : FileFilter {

        override fun accept(file: File): Boolean {

            return file.name.endsWith(".log")
        }
    }

    fun getLogDir(context: Context): File {
        val path = File(context.getExternalFilesDir("logs").absolutePath)
        if (!path.exists()) {
            path.mkdirs()
        } else {
            path.delete()
            path.mkdirs()
        }
        return path
    }
}