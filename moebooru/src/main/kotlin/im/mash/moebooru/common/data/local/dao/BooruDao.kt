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