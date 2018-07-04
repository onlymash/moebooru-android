package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.media.MediaStoreDataSource
import im.mash.moebooru.common.data.media.entity.MediaStoreData
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class MediaRepository(private val source: MediaStoreDataSource,
                      private val scheduler: Scheduler) : MediaDataContract.Repository {

    private val compositeDisposable = CompositeDisposable()

    override val mediaOutcome: PublishSubject<Outcome<MutableList<MediaStoreData>>>
            = PublishSubject.create<Outcome<MutableList<MediaStoreData>>>()

    override fun getMedia(path: String) {
        mediaOutcome.loading(true)
        source.loadMediaData(path)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ data ->
                    mediaOutcome.success(data)
                }, { error -> handleError(error) })
                .addTo(compositeDisposable)
    }

    override fun handleError(error: Throwable) {
        mediaOutcome.failed(error)
    }
}