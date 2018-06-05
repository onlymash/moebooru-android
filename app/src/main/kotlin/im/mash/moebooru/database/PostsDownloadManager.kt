package im.mash.moebooru.database

import im.mash.moebooru.model.DownloadPost
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

class PostsDownloadManager(private val database: DatabaseHelper) {

    companion object {
        private var instance: PostsDownloadManager? = null

        @Synchronized
        fun getInstance(database: DatabaseHelper): PostsDownloadManager {
            if (instance == null) {
                instance = PostsDownloadManager(database)
            }
            return instance!!
        }

        private const val TABLE_NAME = "downloads"
        private const val DOMAIN = "domain"
        private const val ID = "id"
        private const val PREVIEW_URL = "preview_url"
        private const val URL ="url"
        private const val SIZE = "size"
        private const val WIDTH = "width"
        private const val HEIGHT = "height"
        private const val SCORE = "score"
        private const val RATING = "rating"
    }

    private var listener: DownloadListChangeListener? = null

    interface DownloadListChangeListener {
        fun onDownloadListChanged()
    }

    fun setDownloadListChangeListener(listChangeListener: DownloadListChangeListener) {
        listener = listChangeListener
    }

    fun savePosts(posts: MutableList<DownloadPost>) {
        posts.forEach { post ->
            database.use {
                insert(TABLE_NAME,
                        DOMAIN to post.domain,
                        ID to post.id,
                        PREVIEW_URL to post.preview_url,
                        URL to post.url,
                        SIZE to post.size,
                        WIDTH to post.width,
                        HEIGHT to post.height,
                        SCORE to post.score,
                        RATING to post.rating)
            }
        }
        listener?.onDownloadListChanged()
    }

    fun getPost(url: String): DownloadPost? {
        var post: DownloadPost? = null
        database.use {
            select(TABLE_NAME).whereSimple("$URL = ?", url)
                    .parseOpt(object : MapRowParser<DownloadPost> {
                        override fun parseRow(columns: Map<String, Any?>): DownloadPost {
                            post = makeDownloadPost(columns)
                            return post!!
                        }
                    })
        }
        return post
    }

    fun loadPosts(): MutableList<DownloadPost>? {
        val posts: MutableList<DownloadPost> = mutableListOf()
        try {
            database.use {
                select(TABLE_NAME)
                        .parseList(object : MapRowParser<MutableList<DownloadPost>> {
                            override fun parseRow(columns: Map<String, Any?>): MutableList<DownloadPost> {
                                posts.add(makeDownloadPost(columns))
                                return posts
                            }
                        })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (posts.size > 0) { posts } else { null }
    }

    fun deletePosts() {
        database.use {
            execSQL("delete from $TABLE_NAME")
        }
        listener?.onDownloadListChanged()
    }

    fun makeDownloadPost(columns: Map<String, Any?>): DownloadPost {
        val domain: String = columns.get(DOMAIN) as String
        val id: Long = columns.getValue(ID) as Long
        val previewUrl: String = columns.getValue(PREVIEW_URL) as String
        val url: String = columns.getValue(URL) as String
        val size: Long = columns.getValue(SIZE) as Long
        val width: Long = columns.getValue(WIDTH) as Long
        val height: Long = columns.getValue(HEIGHT) as Long
        val score: Long = columns.getValue(SCORE) as Long
        val rating: String = columns.getValue(RATING) as String

        return DownloadPost(domain, id, previewUrl, url, size, width, height, score, rating)
    }
}