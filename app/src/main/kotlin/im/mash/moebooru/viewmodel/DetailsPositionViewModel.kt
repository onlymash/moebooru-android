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
    companion object {
        private var position: MutableLiveData<Int> = MutableLiveData()
    }

    init {
        position.value = 0
    }

    fun getPositionModel(): MutableLiveData<Int> {
        return position
    }

    fun getPosition(): Int {
        return position.value!!
    }

    fun setPosition(pos: Int) {
        position.postValue(pos)
    }
}