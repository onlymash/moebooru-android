package im.mash.moebooru.common.data.local.dao

import androidx.room.*
import im.mash.moebooru.common.data.local.entity.Pool
import io.reactivex.Flowable

@Dao
interface PoolDao {

    @Query("SELECT * FROM pools WHERE host = :host ORDER BY id DESC")
    fun getPools(host: String): Flowable<MutableList<Pool>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBoorus(pools: MutableList<Pool>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBooru(pool: Pool)

    @Delete
    fun deletePool(pool: Pool)

    @Delete
    fun deletePools(pools: MutableList<Pool>)

    @Query("DELETE FROM pools WHERE host = :host AND id NOT IN (SELECT id FROM pools WHERE host = :host ORDER BY id DESC LIMIT :limit)")
    fun deletePools(host: String, limit: Int)
}