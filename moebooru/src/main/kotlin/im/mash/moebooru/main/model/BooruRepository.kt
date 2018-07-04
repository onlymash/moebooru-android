package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.util.logi
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class BooruRepository(private val database: MoeDatabase,
                      private val scheduler: Scheduler) : BooruDataContract.Repository {

    companion object {
        private const val TAG = "BooruRepository"
    }

    private val compositeDisposable = CompositeDisposable()

    override val booruFetchOutcome: PublishSubject<Outcome<MutableList<Booru>>>
            = PublishSubject.create<Outcome<MutableList<Booru>>>()

    override fun loadBoorus() {
        booruFetchOutcome.loading(true)
        database.booruDao()
                .getBoorus()
                .performOnBackOutOnMain(scheduler)
                .subscribe({ boorus ->
                    logi(TAG, "loadBoorus success")
                    booruFetchOutcome.success(boorus)
                }, { error -> handleError(error) })
                .addTo(compositeDisposable)
    }

    override fun deleteBooru(booru: Booru) {
        Completable.fromAction {
            database.booruDao().delete(booru)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun addBooru(booru: Booru) {
        Completable.fromAction {
            database.booruDao().insertBooru(booru)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun addBoorus(boorus: MutableList<Booru>) {
        Completable.fromAction {
            database.booruDao().insertBoorus(boorus)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun handleError(error: Throwable) {
        booruFetchOutcome.failed(error)
    }
}