package im.mash.moebooru.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import im.mash.moebooru.common.model.CommentDataContract

@Suppress("UNCHECKED_CAST")
class CommentViewModelFactory(private val repo: CommentDataContract.Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CommentViewModel(repo) as T
    }
}