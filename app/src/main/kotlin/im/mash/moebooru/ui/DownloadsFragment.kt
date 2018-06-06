package im.mash.moebooru.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.*
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.database.PostsDownloadManager.DownloadListChangeListener
import im.mash.moebooru.download.MoeDownloadListener
import im.mash.moebooru.model.DownloadPost
import im.mash.moebooru.ui.adapter.DownloadsAdapter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DownloadsFragment : ToolbarFragment(), Toolbar.OnMenuItemClickListener {

    companion object {
        private const val TAG = "DownloadsFragment"
    }

    private lateinit var toolbar: Toolbar
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var downloadsView: RecyclerView
    private lateinit var downloadsAdapter: DownloadsAdapter
    private lateinit var downloadListChangeListener: DownloadListChangeListener
    private var posts: MutableList<DownloadPost> = mutableListOf()

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
        toolbar.setOnMenuItemClickListener(this)
        downloadsView = view.findViewById(R.id.rv_downloads)
        downloadsView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayout.VERTICAL, false)
        downloadsView.setHasFixedSize(true)
        downloadsView.addItemDecoration(DividerItemDecoration(this.requireContext(), DividerItemDecoration.VERTICAL))
        downloadsAdapter = DownloadsAdapter(app.downloadController, 0)
        downloadsView.adapter = downloadsAdapter
        initController()
        initDownloadListChangeListener()

        refreshLayout = view.findViewById(R.id.refresh)
        refreshLayout.setColorSchemeResources(
                R.color.blue,
                R.color.purple,
                R.color.green,
                R.color.orange,
                R.color.red
        )
        refreshLayout.setOnRefreshListener {
            refresh()
        }
    }

    private fun refresh() {
        refreshLayout.isRefreshing = true
        doAsync {
            val data = app.downloadManager.loadPosts()
            uiThread {
                refreshLayout.isRefreshing = false
                if (data != null) {
                    posts = data
                    downloadsAdapter.updateSize(0)
                    app.downloadController.updateData(posts)
                    downloadsAdapter.updateSize(posts.size)
                }
            }
        }
    }

    private fun initDownloadListChangeListener() {
        downloadListChangeListener = object : DownloadListChangeListener {
            override fun onDownloadListChanged() {
                doAsync {
                    val data = app.downloadManager.loadPosts()
                    uiThread {
                        if (data != null) {
                            posts = data
                            downloadsAdapter.updateSize(0)
                            app.downloadController.updateData(posts)
                            downloadsAdapter.updateSize(posts.size)
                        }
                    }
                }

            }
        }
//        app.downloadManager.setDownloadListChangeListener(downloadListChangeListener)
    }

    private fun initController() {
        app.downloadController.setDownloadListener(MoeDownloadListener())
        doAsync {
            val data = app.downloadManager.loadPosts()
            uiThread {
                if (data != null) {
                    posts = data
                    app.downloadController.updateData(posts)
                    downloadsAdapter.updateSize(posts.size)
                }
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_start_all -> app.downloadController.startAll(true)
            R.id.action_stop_all -> app.downloadController.stopAll()
            R.id.action_clear_completed -> {
                doAsync {
                    app.downloadController.clearCompleted()
                    val data = app.downloadManager.loadPosts()
                    uiThread {
                        if (data != null) {
                            posts = data
                            app.downloadController.updateData(posts)
                            downloadsAdapter.updateSize(posts.size)
                        } else {
                            posts.clear()
                            app.downloadController.updateData(posts)
                            downloadsAdapter.updateSize(posts.size)
                        }
                    }
                }
            }
            R.id.action_clear_all -> {
                doAsync {
                    app.downloadManager.deletePosts()
                    uiThread {
                        posts.clear()
                        app.downloadController.updateData(posts)
                        downloadsAdapter.updateSize(posts.size)
                    }
                }
            }
        }
        return true
    }
}