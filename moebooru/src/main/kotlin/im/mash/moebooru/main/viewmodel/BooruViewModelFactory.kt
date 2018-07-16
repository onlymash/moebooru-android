package im.mash.moebooru.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import im.mash.moebooru.main.model.BooruDataContract

@Suppress("UNCHECKED_CAST")
class BooruViewModelFactory(private val repository: BooruDataContract.Repository) :
        ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BooruViewModel(repository) as T
    }
}