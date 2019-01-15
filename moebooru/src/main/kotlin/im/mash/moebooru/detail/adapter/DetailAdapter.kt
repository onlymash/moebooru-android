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

package im.mash.moebooru.detail.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import android.util.SparseArray
import android.view.ViewGroup
import im.mash.moebooru.detail.fragment.InfoFragment
import im.mash.moebooru.detail.fragment.PagerFragment
import im.mash.moebooru.detail.fragment.TagFragment

class DetailAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private var fragmentTags: SparseArray<String> = SparseArray()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> InfoFragment()
            1 -> PagerFragment()
            else -> TagFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Fragment {
        val fragment = super.instantiateItem(container, position) as Fragment
        val fragmentTag = fragment.tag
        fragmentTags.put(position, fragmentTag)
        return fragment
    }

    fun notifyFragmentByPosition(position: Int) {
        fragmentTags.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        val fragment = `object` as Fragment
        if (fragmentTags.indexOfValue(fragment.tag) > -1) {
            return super.getItemPosition(`object`)
        }
        return PagerAdapter.POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        fragmentTags.removeAt(position)
    }
}