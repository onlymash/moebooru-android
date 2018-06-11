package im.mash.moebooru.main.model

import android.util.Log
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class BooruRepository(private val local: BooruDataContract.Local,
                      private val scheduler: Scheduler,
                      private val compositeDisposable: CompositeDisposable) : BooruDataContract.Repository {

    companion object {
        private const val TAG = "BooruRepository"
    }

    override val booruFetchOutcome: PublishSubject<Outcome<MutableList<Booru>>> =
            PublishSubject.create<Outcome<MutableList<Booru>>>()

    override fun loadBoorus() {
        booruFetchOutcome.loading(true)
        local.getBoorus()
                .performOnBackOutOnMain(scheduler)
                .subscribe({ boorus ->
                    Log.i(TAG, "LoadBoorus success")
                    booruFetchOutcome.success(boorus)
                }, { error ->
                    handleError(error) })
                .addTo(compositeDisposable)
    }

    override fun deleteBooru(booru: Booru) {
        local.delete(booru)
    }

    override fun addBooru(booru: Booru) {
        local.saveBooru(booru)
    }

    override fun addBoorus(boorus: MutableList<Booru>) {
        local.saveBoorus(boorus)
    }

    override fun handleError(error: Throwable) {
        booruFetchOutcome.failed(error)
    }
}