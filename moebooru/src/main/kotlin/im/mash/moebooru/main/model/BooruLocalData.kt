package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.extensions.performOnBack
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.util.logi
import io.reactivex.Completable
import io.reactivex.Flowable

class BooruLocalData(private val database: MoeDatabase,
                     private val scheduler: Scheduler) : BooruDataContract.Local {

    companion object {
        private const val TAG = "BooruLocalData"
    }
    override fun getBoorus(): Flowable<MutableList<Booru>> {
        return database.booruDao().getBoorus()
    }

    override fun saveBooru(booru: Booru) {
        Completable.fromAction {
            database.booruDao().insertBooru(booru)
        }
                .performOnBack(scheduler)
                .subscribe({}, {error -> logi(TAG, error.message.toString())})
    }

    override fun saveBoorus(boorus: MutableList<Booru>) {
        Completable.fromAction {
            database.booruDao().insertBoorus(boorus)
        }
                .performOnBack(scheduler)
                .subscribe({}, { error -> logi(TAG, error.message.toString()) })
    }

    override fun delete(booru: Booru) {
        Completable.fromAction {
            database.booruDao().delete(booru)
        }
                .performOnBack(scheduler)
                .subscribe({}, {error -> logi(TAG, error.message.toString())})
    }
}