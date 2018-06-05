package im.mash.moebooru.database

import im.mash.moebooru.model.RawPost
import im.mash.moebooru.utils.PostsTable

fun makePost(columns: Map<String, Any?>): RawPost {
    val id: Long = columns.getValue(PostsTable.ID) as Long
    val tags: String? = columns.getValue(PostsTable.TAGS) as? String
    val createdAt: Long? = columns.getValue(PostsTable.CREATE_AT) as? Long
    val creatorId: Long? = columns.getValue(PostsTable.CREATOR_ID) as? Long
    val author: String? = columns.getValue(PostsTable.AUTHOR) as? String
    val change: Long? = columns.getValue(PostsTable.CHANGE) as? Long
    val source: String? = columns.getValue(PostsTable.SOURCE) as? String
    val score: Long = columns.getValue(PostsTable.SCORE) as Long
    val md5: String? = columns.getValue(PostsTable.MD5) as? String
    val fileSize: Long? = columns.getValue(PostsTable.FILE_SIZE) as? Long
    val fileUrl: String? = columns.getValue(PostsTable.FILE_URL) as? String
    var isShownInIndex: Boolean? = false
    if (columns.getValue(PostsTable.IS_SHOWN_IN_INDEX) as? Long == 1L) { isShownInIndex = true }
    val previewUrl: String = columns.getValue(PostsTable.PREVIEW_URL) as String
    val previewWidth: Long? = columns.getValue(PostsTable.PREVIEW_WIDTH) as? Long
    val previewHeight: Long? = columns.getValue(PostsTable.PREVIEW_HEIGHT) as? Long
    val actualPreviewWidth: Long? = columns.getValue(PostsTable.ACTUAL_PREVIEW_WIDTH) as? Long
    val actualPreviewHeight: Long? = columns.getValue(PostsTable.ACTUAL_PREVIEW_HEIGHT) as? Long
    val sampleUrl: String = columns.getValue(PostsTable.SAMPLE_URL) as String
    val sampleWidth: Long? = columns.getValue(PostsTable.SAMPLE_WIDTH) as? Long
    val sampleHeight: Long? = columns.getValue(PostsTable.SAMPLE_HEIGHT) as? Long
    val sampleFileSize: Long? = columns.getValue(PostsTable.SAMPLE_FILE_SIZE) as? Long
    val jpegUrl: String = columns.getValue(PostsTable.JPEG_URL) as String
    val jpegWidth: Long? = columns.getValue(PostsTable.JPEG_WIDTH) as? Long
    val jpegHeight: Long? = columns.getValue(PostsTable.JPEG_HEIGHT) as? Long
    val jpegFileSize: Long? = columns.getValue(PostsTable.JPEG_FILE_SIZE) as? Long
    val rating: String = columns.getValue(PostsTable.RATING) as String
    var hasChildren: Boolean? = false
    if (columns.getValue(PostsTable.HAS_CHILDRE) as? Long == 1L) { hasChildren = true }
    val parentId: Long? = columns.getValue(PostsTable.PARENT_ID) as? Long
    val status: String? = columns.getValue(PostsTable.STATUS) as? String
    val width: Long? = columns.getValue(PostsTable.WIDTH) as? Long
    val height: Long? = columns.getValue(PostsTable.HEIGHT) as? Long
    var isHeld: Boolean? = false
    if (columns.getValue(PostsTable.IS_HELD) as? Long == 1L) { isHeld = true }
    return RawPost(id, tags, createdAt, creatorId, author, change, source, score,
            md5, fileSize, fileUrl, isShownInIndex, previewUrl, previewWidth, previewHeight,
            actualPreviewWidth, actualPreviewHeight, sampleUrl, sampleWidth, sampleHeight, sampleFileSize,
            jpegUrl, jpegWidth, jpegHeight, jpegFileSize, rating, hasChildren, parentId, status,
            width, height, isHeld)
}