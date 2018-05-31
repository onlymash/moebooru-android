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

import im.mash.moebooru.model.Tag
import im.mash.moebooru.utils.TagsTable
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update

class TagsManager(private val database: DatabaseHelper) {

    private var tagsChangeListener: TagsChangeListener? = null

    companion object {
        private var instance: TagsManager? = null
        @Synchronized
        fun getInstance(database: DatabaseHelper): TagsManager {
            if (instance == null) {
                instance = TagsManager(database)
            }
            return instance!!
        }
    }

    interface TagsChangeListener {
        fun onTagsChanged()
    }

    fun setTagsChangeListener(tagsChangeListener: TagsChangeListener) {
        this.tagsChangeListener = tagsChangeListener
    }

    fun saveTag(tag: Tag) {
        database.use {
            insert(TagsTable.TABLE_NAME,
                    TagsTable.SITE to tag.site,
                    TagsTable.NAME to tag.name,
                    TagsTable.IS_SELECTED to tag.is_selected)
        }
        tagsChangeListener?.onTagsChanged()
    }

    fun getSelectedTags(site: Long): MutableList<Tag> {
        val tags: MutableList<Tag> = mutableListOf()
        database.use {
            select(TagsTable.TABLE_NAME)
                    .whereSimple("(${TagsTable.SITE} = ?) and (${TagsTable.IS_SELECTED} = ?)", site.toString(), "1")
                    .parseList(object : MapRowParser<MutableList<Tag>> {
                        override fun parseRow(columns: Map<String, Any?>): MutableList<Tag> {
                            val s: Long = site
                            val name: String = columns.getValue(TagsTable.NAME) as String
                            val isSelected = true
                            val tag = Tag(s, name, isSelected)
                            tags.add(tag)
                            return tags
                        }
                    }
            )
        }
        return tags
    }
    fun getTags(site: Long): MutableList<Tag> {
        val tags: MutableList<Tag> = mutableListOf()
        database.use {
            select(TagsTable.TABLE_NAME)
                    .whereSimple("(${TagsTable.SITE} = ?)", site.toString())
                    .parseList(object : MapRowParser<MutableList<Tag>> {
                        override fun parseRow(columns: Map<String, Any?>): MutableList<Tag> {
                            val s: Long = site
                            val name: String = columns.getValue(TagsTable.NAME) as String
                            var isSelected = false
                            if (columns.getValue(TagsTable.IS_SELECTED) as Long == 1L)  isSelected = true
                            val tag = Tag(s, name, isSelected)
                            tags.add(tag)
                            return tags
                        }
                    }
                    )
        }
        return tags
    }

    fun updateTag(site: Long, name: String, isChecked: Boolean) {
        database.use {
            update(TagsTable.TABLE_NAME, TagsTable.IS_SELECTED to isChecked)
                    .whereSimple("(${TagsTable.SITE} = ?) and (${TagsTable.NAME} = ?)", site.toString(), name)
                    .exec()
        }
    }

    fun deleteTag(site: Long, name: String) {
        database.use {
            execSQL("delete from ${TagsTable.TABLE_NAME} where ${TagsTable.SITE} = $site and ${TagsTable.NAME} = '$name'")
        }
        tagsChangeListener?.onTagsChanged()
    }
}