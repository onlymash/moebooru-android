package im.mash.moebooru.common.data.remote.entity

data class RawUser(
        var name: String,
        var blacklisted_tags: MutableList<String>,
        var id: Int
)