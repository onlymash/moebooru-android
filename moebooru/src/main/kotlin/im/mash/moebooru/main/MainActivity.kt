package im.mash.moebooru.main

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.util.Log
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.common.MoeDH
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.core.application.BaseActivity
import im.mash.moebooru.core.network.Outcome
import im.mash.moebooru.main.viewmodel.PostViewModel
import im.mash.moebooru.main.viewmodel.PostViewModelFactory
import im.mash.moebooru.util.getViewModel
import okhttp3.HttpUrl
import java.io.IOException
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val DRAWER_ITEM_POSTS = 0L
        private const val DRAWER_ITEM_ACCOUNT = 1L
        private const val DRAWER_ITEM_DOWNLOADS = 2L
        private const val DRAWER_ITEM_LOCAL_GALLERY = 3L
        private const val DRAWER_ITEM_SETTINGS = 4L
        private const val DRAWER_ITEM_ABOUT = 5L
    }

    internal lateinit var drawer: Drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var header: AccountHeader
    private lateinit var profileSettingDrawerItem: ProfileSettingDrawerItem
    private var previousSelectedDrawer: Long = 0L    // it's actually lateinit

    private val component by lazy { MoeDH.mainComponent() }

    @Inject
    lateinit var viewModelFactory: PostViewModelFactory
    private val viewModel: PostViewModel by lazy { this.getViewModel<PostViewModel>(viewModelFactory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moebooru)
        Log.i(TAG, "This is MainActivity")
        component.inject(this)

        profileSettingDrawerItem = ProfileSettingDrawerItem()
                .withName(R.string.edit)
                .withIcon(R.drawable.ic_drawer_settings_24dp)

        header = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.background_header)
                .withOnAccountHeaderListener { _, profile, _ ->
                    if (profile.identifier != profileSettingDrawerItem.identifier) {
                        app.settings.activeProfile = profile.identifier
                    } else {

                    }
                    false
                }
                .build()


        val httpUrlBuilder = HttpUrl.Builder()
                .scheme("https")
                .host("konachan.com")
                .addPathSegments("post.json")

        viewModel.postsOutcome.observe(this, Observer<Outcome<MutableList<Post>>> { outcome  ->
            when (outcome) {
                is Outcome.Progress -> {
                    Log.i(TAG, "Outcome.Progress")
                }
                is Outcome.Success -> {
                    Log.i(TAG, "Outcome.Success")
                }
                is Outcome.Failure -> {
                    if (outcome.e is IOException) {
                        outcome.e.printStackTrace()
                    } else {
                        Log.i(TAG, "Outcome.Failure")
                    }
                }
            }
        })
    }

}