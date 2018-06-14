package im.mash.moebooru.download

interface DownloadStatusListener {
    fun onSetTitle(title: String)
    fun onStatusChanged(status: String)
    fun onStatusChanged(statusId: Int)
    fun onProgressNotStart()
    fun onProgressStart(currentOffset: Long, totalLength: Long)
    fun onProgressChange(currentOffset: Long)
    fun onProgressCompleted()
    fun onLoadPreview(url: String)
    fun onStart()
    fun onEnd()
}