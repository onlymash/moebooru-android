package im.mash.moebooru.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import im.mash.moebooru.common.MoeDH

class PositionViewModel : ViewModel() {

    private var position: MutableLiveData<Int> = MutableLiveData()

    fun getPosition(): LiveData<Int> {
        return position
    }

    fun setPosition(position: Int) {
        this.position.value = position
    }

    override fun onCleared() {
        super.onCleared()
        MoeDH.destroyDetailComponent()
    }
}