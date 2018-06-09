package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.local.entity.Tag
import im.mash.moebooru.core.network.Scheduler
import io.reactivex.Flowable

class TagLocalData(private val database: MoeDatabase,
                   private val scheduler: Scheduler) : TagDataContract.Local {
    override fun getTags(site: String): Flowable<MutableList<Tag>> {
        return database.tagDao().getTags(site)
    }

    override fun saveTag(tag: Tag) {

    }

    override fun deleteTag(tag: Tag) {

    }


}