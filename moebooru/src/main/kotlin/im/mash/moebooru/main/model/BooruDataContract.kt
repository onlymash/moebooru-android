package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.scheduler.Outcome
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
}