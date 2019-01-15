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

package im.mash.moebooru.common.data.local.dao

import androidx.room.*
import im.mash.moebooru.common.data.local.entity.Booru
import io.reactivex.Flowable

@Dao
interface BooruDao {

    @Query("SELECT * FROM boorus")
    fun getBoorus(): Flowable<MutableList<Booru>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBoorus(boorus: MutableList<Booru>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooru(booru: Booru)

    @Delete
    fun delete(booru: Booru)
}