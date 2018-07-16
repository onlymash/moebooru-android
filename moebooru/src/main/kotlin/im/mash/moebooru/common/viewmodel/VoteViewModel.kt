package im.mash.moebooru.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import im.mash.moebooru.common.model.VoteDataContract
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.disposables.CompositeDisposable

class VoteViewModel(private val repo: VoteDataContract.Repository,
                    private val compositeDisposable: CompositeDisposable) : ViewModel(){

    val idsOutcomeOneTwo: LiveData<Outcome<MutableList<Int>>> by lazy {
        repo.idsOutcomeOneTwo.toLiveData(compositeDisposable)
    }

    val idsOutcomeThree: LiveData<Outcome<MutableList<Int>>> by lazy {
        repo.idsOutcomeThree.toLiveData(compositeDisposable)
    }

    fun getVoteIdsOneTwo(site: String, username: String) {
        repo.getVoteIdsOneTwo(site, username)
    }

    fun getVoteIdsThree(site: String, username: String) {
        repo.getVoteIdsThree(site, username)
    }

    fun votePost(url: String, id: Int, score: Int, username: String, passwordHash: String) {
        repo.votePost(url, id, score, username, passwordHash)
    }
}