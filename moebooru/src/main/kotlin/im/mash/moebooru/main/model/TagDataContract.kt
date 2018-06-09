package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.local.entity.Tag
import im.mash.moebooru.core.network.Outcome
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject

interface TagDataContract {
    interface Repository {
        val tagFetchOutcome: PublishSubject<Outcome<MutableList<Tag>>>
        fun getTags(site: String)
        fun saveTag(tag: Tag)
        fun deleteTag(tag: Tag)
        fun handleError(error: Throwable)
    }
    interface Local {
        fun getTags(site: String): Flowable<MutableList<Tag>>
        fun saveTag(tag: Tag)
        fun deleteTag(tag: Tag)
    }
}