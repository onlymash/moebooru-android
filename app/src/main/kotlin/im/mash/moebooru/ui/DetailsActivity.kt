/*
 * Copyright (C) 2018 by onlymash <im@mash.im>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package im.mash.moebooru.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.model.RawPost
import im.mash.moebooru.utils.*
import im.mash.moebooru.viewmodel.DetailsPositionViewModel
import im.mash.moebooru.viewmodel.PostsViewModel

class DetailsActivity : BaseActivity() {

    companion object {
        private const val TAG = "DetailsActivity"
    }

    internal var toolbarHeight = 0
    internal var topHeight = 0
    internal var bottomHeight = 0
    internal var tags: String? = null
    internal var currentPostId: Int = 0
    internal lateinit var postsViewModel: PostsViewModel
    internal lateinit var positionViewModel: DetailsPositionViewModel
    internal var items: MutableList<RawPost>? =null

    internal lateinit var toolbarFm: Toolbar
    internal lateinit var bgFm: View

    internal var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moebooru)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.moebooru_layout)) { _, insets ->
            topHeight = insets.systemWindowInsetTop
            bottomHeight = insets.systemWindowInsetBottom
            insets
        }
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            toolbarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }
        postsViewModel = this.getViewModel()
        positionViewModel = this.getViewModel()
        val bundle = intent.getBundleExtra(Key.BUNDLE)
        positionViewModel.setPosition(bundle.getInt(Key.ITEM_POS, 0))
        currentPostId = bundle.getInt(Key.ITEM_ID)
        val type = bundle.getString(Key.TYPE)
        if (type == TableType.SEARCH) {
            tags = bundle.getString(Key.TAGS_SEARCH)
        }
        val detailsFragment = DetailsFragment()
        if (savedInstanceState == null) {
            displayFragment(detailsFragment)
        }
    }

    private fun displayFragment(fragment: ToolbarFragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_moebooru, fragment)
                .commitAllowingStateLoss()
    }

    internal fun setActionBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    internal fun fmWidget(bg: View, toolbar: Toolbar) {
        toolbarFm = toolbar
        bgFm = bg
    }

    fun changeBackground() {
        when (bgFm.visibility) {
            View.GONE -> {
                bgFm.visibility = View.VISIBLE
                toolbarFm.visibility = View.GONE
                hideBar()
            }
            else -> {
                bgFm.visibility = View.GONE
                toolbarFm.visibility = View.VISIBLE
                showBar()
            }
        }
    }

    internal fun showBar() {
        val uiFlags = View.SYSTEM_UI_FLAG_VISIBLE
        window.decorView.systemUiVisibility = uiFlags
    }
    private fun hideBar() {
        val uiFlags = View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.decorView.systemUiVisibility = uiFlags
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            R.id.action_download -> {
                val post = items!![positionViewModel.getPosition()]
                val booru = app.boorusManager.getBooru(app.settings.activeProfile)
                val title = booru.name + " " + post.id
                val url = when (app.settings.postSizeDownload) {
                    Key.POST_SIZE_SAMPLE -> {
                        post.sample_url!!
                    }
                    Key.POST_SIZE_LARGER -> {
                        post.jpeg_url!!
                    }
                    else -> {
                        post.file_url!!
                    }
                }
                val fileName = url.substring(url.lastIndexOf("/") + 1)
                if (mayRequestStoragePermission(this, 0)) {
                    downloadPost(url, title, booru.name, fileName)
                }
            }
            R.id.action_share -> {
                val post = items!![positionViewModel.getPosition()]
                val booru = app.boorusManager.getBooru(app.settings.activeProfile)
                val url = booru.url + "/post/show/" + post.id
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.type = "text/*"
                intent.putExtra(Intent.EXTRA_TEXT, url)
                startActivity(Intent.createChooser(intent, getString(R.string.share_to)))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    internal fun getScreenWidth(): Int {
        val metric: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metric)
        return metric.widthPixels
    }
}
