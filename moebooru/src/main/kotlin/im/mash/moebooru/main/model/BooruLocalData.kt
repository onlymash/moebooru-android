package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.network.Scheduler
import io.reactivex.Flowable

class BooruLocalData(private val database: MoeDatabase,
                     private val scheduler: Scheduler) : BooruDataContract.Local {

    override fun getBoorus(): Flowable<MutableList<Booru>> {
        return database.booruDao().getBoorus()
    }

    override fun saveBooru(booru: Booru) {

    }

    override fun saveBoorus(boorus: MutableList<Booru>) {

    }

    override fun delete(booru: Booru) {

    }
}