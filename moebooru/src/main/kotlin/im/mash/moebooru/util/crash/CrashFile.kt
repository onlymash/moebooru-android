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