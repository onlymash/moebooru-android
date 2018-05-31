package im.mash.moebooru.model

data class RawTag(
        var id: Long?,
        var name: String?,
        var count: Long?,
        var type: String?,
        var ambiguous: Boolean?
)