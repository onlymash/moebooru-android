package im.mash.moebooru.main

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.ActivityNotFoundException
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatDelegate
import android.support.v7.content.res.AppCompatResources
import android.util.Log
import android.view.Gravity
import android.view.View
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.App.Companion.coreComponent
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.common.MoeDH
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.application.BaseActivity
import im.mash.moebooru.core.network.Outcome
import im.mash.moebooru.core.widget.TextDrawable
import im.mash.moebooru.main.fragment.PostFragment
import im.mash.moebooru.helper.getViewModel
import im.mash.moebooru.main.fragment.AboutFragment
import im.mash.moebooru.main.fragment.SettingsFragment
import im.mash.moebooru.main.viewmodel.*
import java.io.IOException
import java.util.*
import javax.inject.Inject

@SuppressLint("RtlHardcoded")
class MainActivity : BaseActivity(), Drawer.OnDrawerItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val TAG = "MainActivity"
        private const val DRAWER_ITEM_POSTS = 0L
        private const val DRAWER_ITEM_ACCOUNT = 1L
        private const val DRAWER_ITEM_DOWNLOADS = 2L
        private const val DRAWER_ITEM_LOCAL_GALLERY = 3L
        private const val DRAWER_ITEM_SETTINGS = 4L
        private const val DRAWER_ITEM_ABOUT = 5L

        private val builder = getTextDrawableBuilder()
        private fun getTextDrawableBuilder(): TextDrawable.Builder {
            val builder = TextDrawable.builder()
            builder.beginConfig().width(50)
            builder.beginConfig().height(50)
            builder.beginConfig().fontSize(30)
            builder.beginConfig().useFont(Typeface.create("sans", Typeface.NORMAL))
            builder.beginConfig().withBorder(2)
            builder.beginConfig().endConfig()
            return builder as TextDrawable.Builder
        }
    }

    internal lateinit var drawer: Drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var header: AccountHeader
    private lateinit var profileSettingDrawerItem: ProfileSettingDrawerItem
    private var previousSelectedDrawer: Long = 0L    // it's actually lateinit

    private val component by lazy { MoeDH.mainComponent() }

    @Inject
    lateinit var booruViewModelFactory: BooruViewModelFactory
    internal val booruViewModel: BooruViewModel by lazy { this.getViewModel<BooruViewModel>(booruViewModelFactory) }

    @Inject
    lateinit var postViewModelFactory: PostViewModelFactory

    @Inject
    lateinit var tagViewModelFactory: TagViewModelFactory

    internal var boorus: MutableList<Booru> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moebooru)
        component.inject(this)

        profileSettingDrawerItem = ProfileSettingDrawerItem()
                .withName(R.string.edit)
                .withIcon(R.drawable.ic_drawer_settings_24dp)

        header = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.background_header)
                .withOnAccountHeaderListener { _, profile, _ ->
                    if (profile.identifier != profileSettingDrawerItem.identifier) {
                        app.settings.activeProfileId = profile.identifier
                        app.settings.activeProfileScheme = boorus[profile.identifier.toInt()].scheme
                        app.settings.activeProfileHost = boorus[profile.identifier.toInt()].host
                    } else {

                    }
                    false
                }
                .build()

        val aboutItem =  PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_ABOUT)
                .withName(R.string.about)
                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_drawer_copyright_24dp))
                .withIconTintingEnabled(true)

        drawerLayout = DrawerLayout(this)

        drawer = DrawerBuilder()
                .withActivity(this)
                .withDrawerLayout(drawerLayout)
                .withTranslucentStatusBar(false)
                .withDrawerGravity(Gravity.LEFT)
                .withAccountHeader(header)
                .addDrawerItems(
                        PrimaryDrawerItem()
                                .withIdentifier(DRAWER_ITEM_POSTS)
                                .withName(R.string.posts)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_drawer_posts_24dp))
                                .withIconTintingEnabled(true),
                        PrimaryDrawerItem()
                                .withIdentifier(DRAWER_ITEM_ACCOUNT)
                                .withName(R.string.title_account)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_drawer_account_circle_24dp))
                                .withIconTintingEnabled(true),
                        PrimaryDrawerItem()
                                .withIdentifier(DRAWER_ITEM_DOWNLOADS)
                                .withName(R.string.downloads)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_drawer_file_download_24dp))
                                .withIconTintingEnabled(true),
                        PrimaryDrawerItem()
                                .withIdentifier(DRAWER_ITEM_LOCAL_GALLERY)
                                .withName(R.string.local_gallery)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_drawer_local_library_24dp))
                                .withIconTintingEnabled(true),
                        PrimaryDrawerItem()
                                .withIdentifier(DRAWER_ITEM_SETTINGS)
                                .withName(R.string.settings)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_drawer_settings_24dp))
                                .withIconTintingEnabled(true)

                )
                .addStickyDrawerItems(
                        aboutItem
                )
                .withStickyFooterDivider(true)
                .withStickyFooterShadow(false)
                .withOnDrawerItemClickListener(this)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withSavedInstance(savedInstanceState)
                .withDrawerWidthDp(300)
                .build()

        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { _, insets ->
            drawer.stickyFooter.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
            insets
        }

        coreComponent.sharedPreferences().registerOnSharedPreferenceChangeListener(this)

        booruViewModel.booruOutcome.observe(this, Observer<Outcome<MutableList<Booru>>> { outcome: Outcome<MutableList<Booru>>? ->
            when (outcome) {
                is Outcome.Progress -> {
                    Log.i(TAG, "Booru Outcome.Progress")
                }
                is Outcome.Success -> {
                    Log.i(TAG, "Booru Outcome.Success")
                    boorus = outcome.data
                    if (boorus.size <= 0) {
                        booruViewModel.addBoorus(mutableListOf<Booru>(
                                Booru(0, 0, "Konachan", "https", "konachan.com", "https://konachan.com"),
                                Booru(1, 1, "Yande.re", "https", "yande.re", "https://yande.re")
                        ))
                    } else {
                        boorus.forEach { booru ->
                            val icon = builder.buildRound(booru.name[0].toString(), getCustomizedColor())
                            val profileDrawerItem: ProfileDrawerItem = ProfileDrawerItem()
                                    .withName(booru.name)
                                    .withEmail(booru.url)
                                    .withIcon(icon)
                            profileDrawerItem.withIdentifier(booru.id.toLong())
                            header.addProfile(profileDrawerItem, booru.id)
                        }
                        header.addProfile(profileSettingDrawerItem, boorus.size)
                        val activeProfileId = app.settings.activeProfileId
                        header.setActiveProfile(activeProfileId)
                        app.settings.activeProfileScheme = boorus[activeProfileId.toInt()].scheme
                        app.settings.activeProfileHost = boorus[activeProfileId.toInt()].host
                        if (savedInstanceState == null) {
                            displayFragment(PostFragment())
                        }
                    }
                }
                is Outcome.Failure -> {
                    if (outcome.e is IOException) {
                        outcome.e.printStackTrace()
                    }
                    Log.i(TAG, "Booru Outcome.Failure")
                }
                null -> {
                    Log.i(TAG, "Booru outcome == null")
                }
            }
        })

        booruViewModel.loadBoorus()

        if (savedInstanceState != null && app.settings.isChangedNightMode) {
            drawer.setSelection(DRAWER_ITEM_SETTINGS)
            app.settings.isChangedNightMode = false
        }
    }

    private fun getCustomizedColor(): Int {
        val customizedColors = resources.getIntArray(R.array.customizedColors)
        return customizedColors[Random().nextInt(customizedColors.size)]
    }

    private fun displayFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_moebooru, fragment)
                .commitAllowingStateLoss()
        drawer.closeDrawer()
    }

    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>?): Boolean {
        val id = drawerItem!!.identifier
        if (id == previousSelectedDrawer) {
            drawer.closeDrawer()
        } else {
            previousSelectedDrawer = id
            when (id) {
                DRAWER_ITEM_POSTS -> displayFragment(PostFragment())
                DRAWER_ITEM_SETTINGS -> displayFragment(SettingsFragment())
                DRAWER_ITEM_ABOUT -> displayFragment(AboutFragment())
            }
        }
        return true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Settings.NIGHT_MODE -> {
                AppCompatDelegate.setDefaultNightMode(app.settings.nightMode)
                app.settings.isChangedNightMode = true
                recreate()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coreComponent.sharedPreferences().unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen) {
            drawer.closeDrawer()
        } else {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_moebooru) as ToolbarFragment
            if (!currentFragment.onBackPressed()) {
                if (currentFragment is PostFragment) {
                    super.onBackPressed()
                } else {
                    drawer.setSelection(DRAWER_ITEM_POSTS)
                }
            }
        }
    }

    private val customTabsIntent by lazy {
        CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.primary))
                .build()
    }

    fun launchUrl(uri: Uri) = try {
        customTabsIntent.launchUrl(this, uri)
    } catch (_: ActivityNotFoundException) { }  // ignore

    fun launchUrl(uri: String) = launchUrl(Uri.parse(uri))
}
