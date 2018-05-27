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

import im.mash.moebooru.model.Booru
import im.mash.moebooru.utils.BoorusTable
import org.jetbrains.anko.db.*

class BoorusManager(private var database: DatabaseHelper) : BoorusSource {

    companion object {
        private var instance: BoorusManager? = null

        @Synchronized
        fun getInstance(database: DatabaseHelper): BoorusManager {
            if (instance == null) {
                instance = BoorusManager(database)
            }
            return instance!!
        }
    }

    override fun saveBooru(booru: Booru) {
        database.use {
            insert(BoorusTable.TABLE_NAME,
                    BoorusTable.ID to booru.id,
                    BoorusTable.NAME to booru.name,
                    BoorusTable.URL to booru.url)
        }
    }

    override fun loadBoorus(): MutableList<Booru> {
        val boorus = mutableListOf<Booru>()
        database.use {
            select(BoorusTable.TABLE_NAME).parseList(
                    object : MapRowParser<MutableList<Booru>> {
                        override fun parseRow(columns: Map<String, Any?>): MutableList<Booru> {
                            val id: Long = columns.getValue(BoorusTable.ID) as Long
                            val name: String = columns.getValue(BoorusTable.NAME) as String
                            val url: String = columns.getValue(BoorusTable.URL) as String
                            val booru = Booru(id, name, url)
                            boorus.add(booru)
                            return boorus
                        }
                    }
            )
        }
        return boorus
    }

    override fun getBooru(id: Long): Booru {
        var booru: Booru? = null
        database.use {
            select(BoorusTable.TABLE_NAME)
                    .whereSimple("${BoorusTable.ID} = ?", id.toString())
                    .parseOpt(object : MapRowParser<Booru> {
                        override fun parseRow(columns: Map<String, Any?>): Booru {
                            val name: String = columns.getValue(BoorusTable.NAME) as String
                            val url: String = columns.getValue(BoorusTable.URL) as String
                            booru = Booru(id, name, url)
                            return booru!!
                        }
                    })
        }
        return booru!!
    }

    override fun deleteBooru(booru: Booru) {
        database.use {
            delete(BoorusTable.TABLE_NAME, "${BoorusTable.ID} = ${booru.id}")
        }
    }
}