package im.mash.moebooru.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import im.mash.moebooru.common.data.local.entity.Pool
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.main.model.PoolDataContract
import io.reactivex.disposables.CompositeDisposable
import okhttp3.HttpUrl

class PoolViewModel(private val repo: PoolDataContract.Repository) : ViewModel() {


    private val compositeDisposable = CompositeDisposable()

    val isEndOutcome: LiveData<Outcome<Boolean>> by lazy {
        repo.isEndOutCome.toLiveData(compositeDisposable)
    }

    val poolsOutcome: LiveData<Outcome<MutableList<Pool>>> by lazy {
        repo.poolFetchOutcome.toLiveData(compositeDisposable)
    }

    fun loadPools(host: String) {
        repo.getPools(host)
    }

    fun refreshPools(url: HttpUrl) {
        repo.refreshPools(url)
    }

    fun loadMorePools(url: HttpUrl) {
        repo.loadMorePools(url)
    }

}