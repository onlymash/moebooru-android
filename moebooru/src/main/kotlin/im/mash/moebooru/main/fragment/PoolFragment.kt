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
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.common.base.LastItemListener
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.common.data.local.entity.Pool
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.helper.getViewModel
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.main.adapter.PoolAdapter
import im.mash.moebooru.main.viewmodel.PoolViewModel
import im.mash.moebooru.search.SearchActivity
import okhttp3.HttpUrl
import retrofit2.HttpException
import java.io.IOException

class PoolFragment : ToolbarFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val TAG = "PoolFragment"
        private const val STATUS_LOADING = 0
        private const val STATUS_REFRESH = 1
        private const val STATUS_LOAD_MORE = 2
        private const val STATUS_IDLE = -1
    }

    private var page = 1
    private var pools: MutableList<Pool> = mutableListOf()
    private var status = STATUS_LOADING

    private var newStart = true

    private lateinit var poolAdapter: PoolAdapter
    private lateinit var poolView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout

    private val mainActivity by lazy { activity as MainActivity }
    private val poolViewModel by lazy { this.getViewModel<PoolViewModel>(mainActivity.poolViewModelFactory) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_pools, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view){ _, insets ->
            val lp = view.layoutParams as FrameLayout.LayoutParams
            lp.setMargins(0, 0, 0, insets.systemWindowInsetBottom)
            view.layoutParams = lp
            insets
        }
        initView(view)
        initViewModel()
        mainActivity.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    private fun initView(view: View) {
        toolbar.setTitle(R.string.pools)
        refreshLayout = view.findViewById(R.id.refresh)
        poolView = view.findViewById(R.id.rv_pools)
        poolView.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        poolView.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        poolAdapter = PoolAdapter()
        poolView.adapter = poolAdapter
        poolView.addOnScrollListener(object : LastItemListener() {
            override fun onLastItemVisible() {
                loadMore()
            }
        })
        poolAdapter.setItemClickListener(object : PoolAdapter.ItemClickListener {
            override fun onClickItem(keyword: String) {
                val intent = Intent(this@PoolFragment.context, SearchActivity::class.java)
                intent.putExtra("keyword", keyword)
                startActivity(intent)
            }
        })
        refreshLayout.setOnRefreshListener {
            if (status == STATUS_IDLE) {
                refresh()
            } else {
                disableRefreshLayout()
            }
        }
    }

    private fun refresh() {
        enableRefreshLayout()
        status = STATUS_REFRESH
        page = 1
        poolViewModel.refreshPools(getHttpUrl())
    }

    private fun loadMore() {
        if (status == STATUS_IDLE) {
            enableRefreshLayout()
            status = STATUS_LOAD_MORE
            page = pools.size/18 + 1
            poolViewModel.loadMorePools(getHttpUrl())
        }
    }

    private fun initViewModel() {
        poolViewModel.poolsOutcome.observe(this, Observer<Outcome<MutableList<Pool>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {}
                is Outcome.Success -> {
                    disableRefreshLayout()
                    val data = outcome.data
                    when (status) {
                        STATUS_LOADING -> {
                            pools = data
                            poolAdapter.updateData(pools)
                            if (pools.isEmpty() && newStart) {
                                newStart = false
                                refresh()
                            }
                        }
                        STATUS_REFRESH -> {
                            pools = data
                            poolAdapter.updateData(pools)
                        }
                        STATUS_LOAD_MORE -> {
                            pools = data
                            poolAdapter.updateData(pools)
                        }
                    }
                    status = STATUS_IDLE
                }
                is Outcome.Failure -> {
                    status = STATUS_IDLE
                    val error = outcome.e
                    when (error) {
                        is HttpException -> {
                            Toast.makeText(this.requireContext(), "code: ${error.code()}, msg: ${error.message()}", Toast.LENGTH_SHORT).show()
                        }
                        is IOException -> {
                            error.printStackTrace()
                        }
                    }
                }
            }
        })

        poolViewModel.isEndOutcome.observe(this, Observer<Outcome<Boolean>> { outcome ->
            when (outcome) {
                is Outcome.Success -> {
                    if (outcome.data) {
                        disableRefreshLayout()
                    }
                }
            }
        })

        poolViewModel.loadPools(app.settings.activeProfileHost)
    }

    private fun getHttpUrl(): HttpUrl {
        return HttpUrl.Builder()
                .scheme(app.settings.activeProfileSchema)
                .host(app.settings.activeProfileHost)
                .addPathSegment("pool.json")
                .addQueryParameter("page",  page.toString())
                .build()
    }

    private fun disableRefreshLayout() {
        if (refreshLayout.isRefreshing) {
            refreshLayout.isRefreshing = false
        }
    }

    private fun enableRefreshLayout() {
        if (!refreshLayout.isRefreshing) {
            refreshLayout.isRefreshing = true
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Settings.ACTIVE_PROFILE_HOST -> {
                pools.clear()
                mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_moebooru, PoolFragment())
                        .commitAllowingStateLoss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}