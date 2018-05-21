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
import im.mash.moebooru.utils.PostsTable
import im.mash.moebooru.utils.SearchTagsTable
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
                BoorusTable.ID_UNIQUE to INTEGER + PRIMARY_KEY + UNIQUE,
                BoorusTable.ID to INTEGER + UNIQUE,
                BoorusTable.NAME to TEXT,
                BoorusTable.URL to TEXT)
        db?.createTable(
                SearchTagsTable.TABLE_NAME,
                true,
                SearchTagsTable.ID_UNIQUE to INTEGER + PRIMARY_KEY + UNIQUE,
                SearchTagsTable.SITE to INTEGER,
                SearchTagsTable.NAME to TEXT,
                SearchTagsTable.IS_SELECTED to INTEGER
        )
        db?.createTable(
                PostsTable.TABLE_NAME,
                true,
                PostsTable.ID_UNIQUE to INTEGER + PRIMARY_KEY + UNIQUE,
                PostsTable.SITE to INTEGER,
                PostsTable.ID to INTEGER,
                PostsTable.TAGS to TEXT,
                PostsTable.CREATE_AT to INTEGER,
                PostsTable.CREATOR_ID to INTEGER,
                PostsTable.AUTHOR to TEXT,
                PostsTable.CHANGE to INTEGER,
                PostsTable.SOURCE to TEXT,
                PostsTable.SCORE to INTEGER,
                PostsTable.MD5 to TEXT,
                PostsTable.FILE_SIZE to INTEGER,
                PostsTable.FILE_URL to TEXT,
                PostsTable.IS_SHOWN_IN_INDEX to INTEGER,
                PostsTable.PREVIEW_URL to TEXT,
                PostsTable.PREVIEW_WIDTH to INTEGER,
                PostsTable.PREVIEW_HEIGHT to INTEGER,
                PostsTable.ACTUAL_PREVIEW_WIDTH to INTEGER,
                PostsTable.ACTUAL_PREVIEW_HEIGHT to INTEGER,
                PostsTable.SAMPLE_URL to TEXT,
                PostsTable.SAMPLE_WIDTH to INTEGER,
                PostsTable.SAMPLE_HEIGHT to INTEGER,
                PostsTable.SAMPLE_FILE_SIZE to INTEGER,
                PostsTable.JPEG_URL to TEXT,
                PostsTable.JPEG_WIDTH to INTEGER,
                PostsTable.JPEG_HEIGHT to INTEGER,
                PostsTable.JPEG_FILE_SIZE to INTEGER,
                PostsTable.RATING to TEXT,
                PostsTable.HAS_CHILDRE to INTEGER,
                PostsTable.PARENT_ID to INTEGER,
                PostsTable.STATUS to TEXT,
                PostsTable.WIDTH to INTEGER,
                PostsTable.HEIGHT to INTEGER,
                PostsTable.IS_HELD to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.dropTable(BoorusTable.TABLE_NAME, true)
        db?.dropTable(SearchTagsTable.TABLE_NAME, true)
        db?.dropTable(PostsTable.TABLE_NAME, true)
    }
}