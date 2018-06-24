package im.mash.moebooru.common.model

import okhttp3.HttpUrl

interface VoteDataContract {

    interface Repository {
        fun votePost(url: HttpUrl)
        fun handleError(error: Throwable)
    }
}