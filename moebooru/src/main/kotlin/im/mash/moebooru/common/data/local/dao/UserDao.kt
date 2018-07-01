package im.mash.moebooru.common.data.local.dao

import android.arch.persistence.room.*
import im.mash.moebooru.common.data.local.entity.User
import io.reactivex.Flowable

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun loadUsers(): Flowable<MutableList<User>>

    @Query("SELECT * FROM user WHERE url = :url")
    fun loadUser(url: String): Flowable<MutableList<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Update
    fun updateUser(user: User)

    @Delete
    fun deleteUser(user: User)


}