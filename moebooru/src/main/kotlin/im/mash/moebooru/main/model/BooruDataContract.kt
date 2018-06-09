package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.network.Outcome
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject

interface BooruDataContract {
    interface Repository {
        val booruFetchOutcome: PublishSubject<Outcome<MutableList<Booru>>>
        fun loadBoorus()
        fun addBooru(booru: Booru)
        fun addBoorus(boorus: MutableList<Booru>)
        fun deleteBooru(booru: Booru)
        fun handleError(error: Throwable)
    }
    interface Local {
        fun getBoorus(): Flowable<MutableList<Booru>>
        fun saveBooru(booru: Booru)
        fun saveBoorus(boorus: MutableList<Booru>)
        fun delete(booru: Booru)
    }
}