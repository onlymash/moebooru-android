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

package im.mash.moebooru.common.base

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager

//https://stackoverflow.com/a/40526380

abstract class LastItemListener : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        // init
        val layoutManager = recyclerView.layoutManager ?: return
        val adapter = recyclerView.adapter ?: return

        if (layoutManager.childCount > 0) {
            // Calculations..
            val indexOfLastItemViewVisible = layoutManager.childCount - 1
            val lastItemViewVisible = layoutManager.getChildAt(indexOfLastItemViewVisible) ?: return
            val adapterPosition = layoutManager.getPosition(lastItemViewVisible)
            val isLastItemVisible = adapterPosition == adapter.itemCount - 1

            // check
            if (isLastItemVisible)
                onLastItemVisible() // callback
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
//        if (recyclerView.layoutManager is StaggeredGridLayoutManager) {
//            (recyclerView.layoutManager as StaggeredGridLayoutManager).invalidateSpanAssignments()
//        }
    }

    /**
     * Here you should load more items because user is seeing the last item of the list.
     * Advice: you should add a bollean value to the class
     * so that the method [.onLastItemVisible] will be triggered only once
     * and not every time the user touch the screen ;)
     */
    abstract fun onLastItemVisible()

}
