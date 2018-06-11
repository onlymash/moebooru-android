package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.media.entity.MediaStoreData
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.subjects.PublishSubject

interface MediaDataContract {
    interface Repository {
        val mediaOutcome: PublishSubject<Outcome<MutableList<MediaStoreData>>>
        fun getMedia(path: String)
        fun handleError(error: Throwable)
    }
}