package im.mash.moebooru.main.fragment

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.common.data.local.entity.PostDownload
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.download.DownloadService
import im.mash.moebooru.helper.getViewModel
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.main.adapter.DownloadAdapter
import im.mash.moebooru.main.viewmodel.DownloadViewModel
import im.mash.moebooru.util.logi

class DownloadFragment : ToolbarFragment(), Toolbar.OnMenuItemClickListener {

    companion object {
        private const val TAG = "DownloadFragment"
    }

    private lateinit var downloadView: RecyclerView
    private lateinit var downloadAdapter: DownloadAdapter
    private var posts: MutableList<PostDownload> = mutableListOf()

    private val mainActivity: MainActivity by lazy { activity as MainActivity }
    private val downloadViewModel: DownloadViewModel by lazy {
        this.getViewModel<DownloadViewModel>(mainActivity.downloadViewModelFactory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DownloadService.init(this.requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_downloads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view){ _, insets ->
            view.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
            insets
        }
        toolbar.setTitle(R.string.downloads)
        toolbar.inflateMenu(R.menu.menu_downloads)
        toolbar.setOnMenuItemClickListener(this)
        downloadView = view.findViewById(R.id.rv_downloads)
        downloadView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayout.VERTICAL, false)
        downloadView.setHasFixedSize(true)
        downloadView.addItemDecoration(DividerItemDecoration(this.requireContext(), DividerItemDecoration.VERTICAL))
        downloadAdapter = DownloadAdapter(this.requireContext(), app.downloadManager)
        downloadView.adapter = downloadAdapter

        downloadViewModel.downloadPostsOutcome.observe(this,
                Observer<Outcome<MutableList<PostDownload>>> { outcome ->
                    when (outcome) {
                        is Outcome.Progress -> {
                            logi(TAG, "Outcome.Progress")
                        }

                        is Outcome.Success -> {
                            posts = outcome.data
                            if (app.downloadManager.getCount() != posts.size) {
                                app.downloadManager.updateData(posts)
                            }
                            downloadAdapter.updateData(posts)
                            logi(TAG, "Outcome.Success. posts.size: ${posts.size}")
                        }

                        is Outcome.Failure -> {
                            logi(TAG, "Outcome.Failure")
                        }
                    }
                })
        downloadViewModel.loadAll()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_start_all -> app.downloadManager.startAll(true)
            R.id.action_stop_all -> app.downloadManager.stopAll()
            R.id.action_clear_completed -> {
                downloadViewModel.delete(app.downloadManager.getCompleted())
            }
            R.id.action_clear_all -> {
                app.downloadManager.stopAll()
                downloadViewModel.deleteAll()
            }
        }
        return true
    }
}