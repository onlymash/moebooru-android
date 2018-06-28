package im.mash.moebooru.common.data.remote.entity

import im.mash.moebooru.common.data.local.entity.PostSearch

data class Vote(
        var success: Boolean,
        var posts: MutableList<PostSearch> = mutableListOf()
)