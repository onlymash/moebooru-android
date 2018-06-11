package im.mash.moebooru.detail.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class PositionViewModel : ViewModel() {

    private var position: MutableLiveData<Int> = MutableLiveData()

    fun getPosition(): LiveData<Int> {
        return position
    }

    fun setPosition(position: Int) {
        this.position.value = position
    }
}