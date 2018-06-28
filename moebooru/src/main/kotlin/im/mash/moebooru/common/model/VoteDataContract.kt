package im.mash.moebooru.common.model

import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.subjects.PublishSubject

interface VoteDataContract {

    interface Repository {
        val idsOutcomeOneTwo: PublishSubject<Outcome<MutableList<Int>>>
        val idsOutcomeThree: PublishSubject<Outcome<MutableList<Int>>>
        fun votePost(url: String, id: Int, score: Int, username: String, passwordHash: String)
        fun getVoteIdsOneTwo(site: String, username: String)
        fun getVoteIdsThree(site: String, username: String)
        fun handleErrorOneTwo(error: Throwable)
        fun handleErrorThree(error: Throwable)
    }
}