package im.mash.moebooru.database

import im.mash.moebooru.model.RawPost
import im.mash.moebooru.utils.DownloadsTable
import im.mash.moebooru.utils.PostsTable
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
    }

    private var listener: DownloadListChangeListener? = null

    interface DownloadListChangeListener {
        fun onDownloadListChanged()
    }

    fun setDownloadListChangeListener(listChangeListener: DownloadListChangeListener) {
        listener = listChangeListener
    }

    fun savePosts(posts: MutableList<RawPost>, site: Long) {
        posts.forEach {
            database.use {
                insert(DownloadsTable.TABLE_NAME,
                        PostsTable.SITE to site,
                        PostsTable.ID to it.id,
                        PostsTable.TAGS to it.tags,
                        PostsTable.CREATE_AT to it.created_at,
                        PostsTable.CREATOR_ID to it.creator_id,
                        PostsTable.AUTHOR to it.author,
                        PostsTable.CHANGE to it.change,
                        PostsTable.SOURCE to it.source,
                        PostsTable.SCORE to it.score,
                        PostsTable.MD5 to it.md5,
                        PostsTable.FILE_SIZE to it.file_size,
                        PostsTable.FILE_URL to it.file_url,
                        PostsTable.IS_SHOWN_IN_INDEX to it.is_shown_in_index,
                        PostsTable.PREVIEW_URL to it.preview_url,
                        PostsTable.PREVIEW_WIDTH to it.preview_width,
                        PostsTable.PREVIEW_HEIGHT to it.preview_height,
                        PostsTable.ACTUAL_PREVIEW_WIDTH to it.actual_preview_width,
                        PostsTable.ACTUAL_PREVIEW_HEIGHT to it.actual_preview_height,
                        PostsTable.SAMPLE_URL to it.sample_url,
                        PostsTable.SAMPLE_WIDTH to it.sample_width,
                        PostsTable.SAMPLE_HEIGHT to it.sample_height,
                        PostsTable.SAMPLE_FILE_SIZE to it.sample_file_size,
                        PostsTable.JPEG_URL to it.jpeg_url,
                        PostsTable.JPEG_WIDTH to it.jpeg_width,
                        PostsTable.JPEG_HEIGHT to it.jpeg_height,
                        PostsTable.JPEG_FILE_SIZE to it.jpeg_file_size,
                        PostsTable.RATING to it.rating,
                        PostsTable.HAS_CHILDRE to it.has_children,
                        PostsTable.PARENT_ID to it.parent_id,
                        PostsTable.STATUS to it.status,
                        PostsTable.WIDTH to it.width,
                        PostsTable.HEIGHT to it.height,
                        PostsTable.IS_HELD to it.is_held)
            }
        }
        listener?.onDownloadListChanged()
    }

    fun getPost(site: Long): RawPost? {
        val post: RawPost? = null
        database.use {
            select(DownloadsTable.TABLE_NAME)
                    .whereSimple("${PostsTable.SITE} = ?", site.toString())
        }
        return post
    }

    fun getPostFromId(site: Long, id: Int): RawPost? {
        var post: RawPost? = null
        if (id > -1) {
            database.use {
                select(DownloadsTable.TABLE_NAME)
                        .whereSimple("(${PostsTable.SITE} = ?) and (${PostsTable.ID} = ?)", site.toString(), id.toString())
                        .parseOpt(object : MapRowParser<RawPost> {
                            override fun parseRow(columns: Map<String, Any?>): RawPost {
                                post = makePost(columns)
                                return post!!
                            }
                        })
            }
        }
        return post
    }

    fun loadPosts(site: Long): MutableList<RawPost>? {
        val posts: MutableList<RawPost> = mutableListOf()
        try {
            database.use {
                select(DownloadsTable.TABLE_NAME)
                        .whereSimple("${PostsTable.SITE} = ?", site.toString())
                        .parseList(object : MapRowParser<MutableList<RawPost>> {
                            override fun parseRow(columns: Map<String, Any?>): MutableList<RawPost> {
                                posts.add(makePost(columns))
                                return posts
                            }
                        })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (posts.size > 0) { posts } else { null }
    }

    fun deletePosts(site: Long) {
        database.use {
            execSQL("delete from ${DownloadsTable.TABLE_NAME} where ${PostsTable.SITE} = $site")
        }
        listener?.onDownloadListChanged()
    }
}