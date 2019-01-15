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

import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.mash.moebooru.R
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.util.childFragManager

class SettingsFragment : ToolbarFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_settings, container, false)
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(activity as MainActivity, R.color.window_background))
        toolbar.setTitle(R.string.settings)
        val fm = childFragmentManager
        fm.beginTransaction().replace(R.id.content, SettingsPreferenceFragment()).commit()
        fm.executePendingTransactions()
    }

    override fun onDetach() {
        super.onDetach()
        childFragManager = null
    }
}