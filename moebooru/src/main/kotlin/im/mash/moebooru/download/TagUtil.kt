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

package im.mash.moebooru.download

import com.liulishuo.okdownload.DownloadTask

object TagUtil {

    private const val KEY_STATUS = 0
    private const val KEY_OFFSET = 1
    private const val KEY_TOTAL = 2
    private const val KEY_TASK_NAME = 3
    private const val KEY_PRIORITY = 4

    internal fun saveStatus(task: DownloadTask, status: String) {
        task.addTag(KEY_STATUS, status)
    }

    internal fun getStatus(task: DownloadTask): String? {
        val status = task.getTag(KEY_STATUS)
        return if (status != null) status as String else null
    }

    internal fun saveOffset(task: DownloadTask, offset: Long) {
        task.addTag(KEY_OFFSET, offset)
    }

    internal fun getOffset(task: DownloadTask): Long {
        val offset = task.getTag(KEY_OFFSET)
        return if (offset != null) offset as Long else 0
    }

    internal fun saveTotal(task: DownloadTask, total: Long) {
        task.addTag(KEY_TOTAL, total)
    }

    internal fun getTotal(task: DownloadTask): Long {
        val total = task.getTag(KEY_TOTAL)
        return if (total != null) total as Long else 0
    }

    internal fun saveTaskName(task: DownloadTask, name: String) {
        task.addTag(KEY_TASK_NAME, name)
    }

    internal fun getTaskName(task: DownloadTask): String? {
        val taskName = task.getTag(KEY_TASK_NAME)
        return if (taskName != null) taskName as String else null
    }

    internal fun savePriority(task: DownloadTask, priority: Int) {
        task.addTag(KEY_PRIORITY, priority)
    }

    internal fun getPriority(task: DownloadTask): Int {
        val priority = task.getTag(KEY_PRIORITY)
        return if (priority != null) priority as Int else 0
    }

    internal fun clearProceedTask(task: DownloadTask) {
        task.removeTag(KEY_STATUS)
        task.removeTag(KEY_OFFSET)
        task.removeTag(KEY_TOTAL)
        task.removeTag(KEY_OFFSET)
    }
}