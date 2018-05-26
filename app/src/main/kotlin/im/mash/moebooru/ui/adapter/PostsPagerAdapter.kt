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

package im.mash.moebooru.ui.adapter

import android.graphics.drawable.Drawable
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.bm.library.PhotoView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.model.RawPost
import im.mash.moebooru.ui.DetailsFragment
import im.mash.moebooru.utils.glideHeader
import kotlinx.android.synthetic.main.layout_details.*

class PostsPagerAdapter(private val detailsFragment: DetailsFragment, private var items: MutableList<RawPost>?) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return items?.size?:0
    }

    fun updateData(items: MutableList<RawPost>?) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoView = PhotoView(detailsFragment.requireContext())
        photoView.enable()
        detailsFragment.progress_bar.visibility = View.VISIBLE
        photoView.setOnClickListener {
            detailsFragment.onClickPhotoView()
        }
        GlideApp.with(detailsFragment.requireContext())
                .load(GlideUrl(items?.get(position)?.sample_url, glideHeader))
                .fitCenter()
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?,
                                                 target: Target<Drawable>?, dataSource: DataSource?,
                                                 isFirstResource: Boolean): Boolean {

                        detailsFragment.progress_bar.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?,
                                              target: Target<Drawable>?,
                                              isFirstResource: Boolean): Boolean {

                        detailsFragment.progress_bar.visibility = View.GONE
                        return false
                    }

                })
                .into(photoView)
        container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        return photoView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}