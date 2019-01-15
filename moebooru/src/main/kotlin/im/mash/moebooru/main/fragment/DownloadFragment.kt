/*
 * Copyright (C) 2019 by onlymash <im@fiepi.me>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package im.mash.moebooru.main.fragment

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import im.mash.moebooru.common.viewmodel.DownloadViewModel
import im.mash.moebooru.util.logi
import im.mash.moebooru.util.mayRequestStoragePermission

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
            val lp = view.layoutParams as FrameLayout.LayoutParams
            lp.setMargins(0, 0, 0, insets.systemWindowInsetBottom)
            view.layoutParams = lp
            insets
        }
        toolbar.setTitle(R.string.downloads)
        toolbar.inflateMenu(R.menu.menu_downloads)
        toolbar.setOnMenuItemClickListener(this)
        downloadView = view.findViewById(R.id.rv_downloads)
        downloadView.layoutManager = LinearLayoutManager(this.requireContext(), RecyclerView.VERTICAL, false)
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
        if (mayRequestStoragePermission(mainActivity, 0)) {
            downloadViewModel.loadAll()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_start_all -> {
                if (posts.size > 0) {
                    app.downloadManager.startAll(true)
                }
            }
            R.id.action_stop_all -> {
                if (posts.size > 0) {
                    app.downloadManager.stopAll()
                }
            }
            R.id.action_clear_completed -> {
                if (posts.size > 0) {
                    downloadViewModel.delete(app.downloadManager.getCompleted())
                }
            }
            R.id.action_clear_all -> {
                if (posts.size > 0) {
                    app.downloadManager.stopAll()
                    downloadViewModel.deleteAll()
                }
            }
        }
        return true
    }
}