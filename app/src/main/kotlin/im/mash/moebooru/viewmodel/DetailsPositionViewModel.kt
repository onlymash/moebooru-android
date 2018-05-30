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

package im.mash.moebooru.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class DetailsPositionViewModel : ViewModel() {
    private var position: MutableLiveData<Int>? =null

    fun getPosition(): MutableLiveData<Int> {
        if (position == null) {
            position = MutableLiveData()
        }
        return position!!
    }

    fun setPosition(pos: Int) {
        if (position == null) {
            position = MutableLiveData()
        }
        position!!.postValue(pos)
    }
}