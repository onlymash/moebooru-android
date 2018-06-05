package im.mash.moebooru.model

data class DownloadPost(
        val domain: String,
        val id: Long,
        val preview_url: String,
        val url: String,
        val size: Long,
        val width: Long,
        val height: Long,
        val score: Long,
        val rating: String

)