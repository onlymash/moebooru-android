package im.mash.moebooru.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity

inline fun <reified M : ViewModel> Fragment.getViewModel(viewModelFactory: ViewModelProvider.Factory): M {
    return ViewModelProvider(this, viewModelFactory).get(M::class.java)
}

inline fun <reified M : ViewModel> AppCompatActivity.getViewModel(viewModelFactory: ViewModelProvider.Factory): M {
    return ViewModelProvider(this, viewModelFactory).get(M::class.java)
}

inline fun <reified M : ViewModel> Fragment.getViewModel(): M {
    val application = activity?.application
            ?: throw IllegalStateException("Fragment is not attached to activity")
    val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    return ViewModelProvider(this, factory).get(M::class.java)
}

inline fun <reified M : ViewModel> AppCompatActivity.getViewModel(): M {
    val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    return ViewModelProvider(this, factory).get(M::class.java)
}