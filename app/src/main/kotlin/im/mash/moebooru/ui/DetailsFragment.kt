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

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.models.RawPost
import im.mash.moebooru.ui.widget.MoeImageView
import im.mash.moebooru.utils.Key
import im.mash.moebooru.utils.glideHeader
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DetailsFragment : ToolbarFragment() {

    private val TAG = this::class.java.simpleName
    private var post: RawPost? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.primary))
        toolbar.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.toolbar_post))

        val moeImageView = view.findViewById<MoeImageView>(R.id.post_img)
        moeImageView.enable()

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)

        val bundle = arguments
        if (bundle != null) {
            val pos = bundle.getInt(Key.BUNDLE, -2)
            val id = bundle.getInt(Key.ITEM_ID)
            Log.i(TAG, "接收位置： $pos")
            doAsync {
                post = app.postsManager.getPostFromId(app.settings.activeProfile, id)
                uiThread {
                    if (post != null) {
                        Log.i(TAG, "查寻成功！ URL： ${post!!.file_url}")
                        GlideApp.with(this@DetailsFragment)
                                .load(GlideUrl(post!!.jpeg_url, glideHeader))
                                .fitCenter()
                                .listener(object : RequestListener<Drawable> {
                                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                        progressBar.visibility = View.GONE
                                        return false
                                    }

                                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                        progressBar.visibility = View.GONE
                                        return false
                                    }

                                })
                                .into(moeImageView)

                    } else {
                        Log.i(TAG, "查寻大失败！ post == null")
                    }
                }
            }
        }
    }
}