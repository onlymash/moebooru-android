package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.local.entity.Tag
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.subjects.PublishSubject

interface TagDataContract {
    interface Repository {
        val tagFetchOutcome: PublishSubject<Outcome<MutableList<Tag>>>
        fun getTags(site: String)
        fun saveTag(tag: Tag)
        fun deleteTag(tag: Tag)
        fun handleError(error: Throwable)
    }
}