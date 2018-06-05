package im.mash.moebooru.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.liulishuo.okdownload.DownloadContextListener
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.database.PostsDownloadManager.DownloadListChangeListener
import im.mash.moebooru.download.MoeDownloadController
import im.mash.moebooru.download.MoeDownloadListener
import im.mash.moebooru.model.RawPost
import im.mash.moebooru.ui.adapter.DownloadsAdapter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DownloadsFragment : ToolbarFragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var downloadsView: RecyclerView
    private lateinit var downloadsAdapter: DownloadsAdapter
    private lateinit var controller: MoeDownloadController
    private lateinit var downloadListChangeListener: DownloadListChangeListener
    private var posts: MutableList<RawPost> = mutableListOf()

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        toolbar = inflater.inflate(R.layout.layout_toolbar, null) as Toolbar
        return inflater.inflate(R.layout.layout_downloads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view){_, insets ->
            view.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
            insets
        }
        toolbar.setTitle(R.string.downloads)
        toolbar.inflateMenu(R.menu.menu_downloads)
        setInsetsListener(toolbar)
        downloadsView = view.findViewById(R.id.rv_downloads)
        downloadsView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayout.VERTICAL, false)
        downloadsView.hasFixedSize()
        downloadsView.addItemDecoration(DividerItemDecoration(this.requireContext(), DividerItemDecoration.VERTICAL))
        initController()
        initDownloadListChangeListener()
    }

    private fun initDownloadListChangeListener() {
        downloadListChangeListener = object : DownloadListChangeListener {
            override fun onDownloadListChanged() {
                doAsync {
                    val data = app.downloadManager.loadPosts(app.settings.activeProfile)
                    uiThread {
                        if (data != null) {
                            posts = data
                            controller.updateData(posts)
                            downloadsAdapter.notifyDataSetChanged()
                        }
                    }
                }

            }
        }
        app.downloadManager.setDownloadListChangeListener(downloadListChangeListener)
    }

    private fun initController() {

        controller = MoeDownloadController()
        controller.setDownloadListener(MoeDownloadListener())
        downloadsAdapter = DownloadsAdapter(controller)
        downloadsView.adapter = downloadsAdapter
        doAsync {
            val data = app.downloadManager.loadPosts(app.settings.activeProfile)
            uiThread {
                if (data != null) {
                    posts = data
                    controller.updateData(posts)
                    downloadsAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}