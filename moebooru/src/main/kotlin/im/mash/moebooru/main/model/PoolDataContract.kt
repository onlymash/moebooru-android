package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.local.entity.Pool
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.subjects.PublishSubject
import okhttp3.HttpUrl

interface PoolDataContract {
    interface Repository {
        val poolFetchOutcome: PublishSubject<Outcome<MutableList<Pool>>>
        fun getPools(host: String)
        fun savePools(pools: MutableList<Pool>)
        fun refreshPools(url: HttpUrl)
        fun loadMorePools(url: HttpUrl)
        fun deletePools(host: String, limit: Int)
        fun handleError(error: Throwable)
    }
}