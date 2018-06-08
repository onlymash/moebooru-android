package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.extensions.addTo
import im.mash.moebooru.core.extensions.failed
import im.mash.moebooru.core.extensions.performOnBackOutOnMain
import im.mash.moebooru.core.extensions.success
import im.mash.moebooru.core.network.Outcome
import im.mash.moebooru.core.network.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class BooruRepository(private val local: BooruDataContract.Local,
                      private val scheduler: Scheduler,
                      private val compositeDisposable: CompositeDisposable) : BooruDataContract.Repository {
    override val booruFetchOutcome: PublishSubject<Outcome<MutableList<Booru>>>
        get() = PublishSubject.create<Outcome<MutableList<Booru>>>()

    override fun loadBoorus() {
        local.getBoorus()
                .performOnBackOutOnMain(scheduler)
                .subscribe({ boorus ->
                    booruFetchOutcome.success(boorus)
                }, { error ->
                    handleError(error) })
                .addTo(compositeDisposable)
    }

    override fun deleteBooru(booru: Booru) {
        local.delete(booru)
    }

    override fun handleError(error: Throwable) {
        booruFetchOutcome.failed(error)
    }

}