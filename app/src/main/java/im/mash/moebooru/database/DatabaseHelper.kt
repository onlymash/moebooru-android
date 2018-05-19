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

package im.mash.moebooru.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import im.mash.moebooru.utils.BoorusTable
import org.jetbrains.anko.db.*

class DatabaseHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, DB_NAME, null, DB_VERSION) {

    companion object {
        private var instance: DatabaseHelper? = null

        @Synchronized
        fun getInstance(ctx: Context) : DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(ctx.applicationContext)
            }
            return instance!!
        }

        const val DB_NAME = "database.db"

        const val DB_VERSION = 1
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(
                BoorusTable.TABLE_NAME,
                true,
                BoorusTable.ID to INTEGER + PRIMARY_KEY + UNIQUE,
                BoorusTable.NAME to TEXT,
                BoorusTable.URL to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}
val Context.database: DatabaseHelper
    get() = DatabaseHelper.getInstance(applicationContext)