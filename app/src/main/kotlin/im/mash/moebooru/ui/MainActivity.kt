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

package im.mash.moebooru.ui

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.content.res.AppCompatResources
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
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
import im.mash.moebooru.R
import im.mash.moebooru.model.Booru
import im.mash.moebooru.model.TextDrawable
import im.mash.moebooru.utils.Key
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

@SuppressLint("RtlHardcoded")
class MainActivity : AppCompatActivity(), Drawer.OnDrawerItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
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

    private val TAG = this.javaClass.simpleName

    internal lateinit var drawer: Drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var header: AccountHeader
    private lateinit var profileSettingDrawerItem: ProfileSettingDrawerItem
    private var previousSelectedDrawer: Long = 0L    // it's actually lateinit

    internal var widthScreen: Int = 0
    internal var toolbarHeight = 0

    private val boorus: MutableList<Booru> = mutableListOf()

    private fun loadAsync() {
        doAsync {
            boorus.addAll(app.boorusManager.loadBoorus())
            if (boorus.isEmpty()) {
                Log.i(TAG, "boorus.isEmpty()")
                app.boorusManager.saveBooru(Booru(0L, "Konachan", "https://konachan.com"))
                app.boorusManager.saveBooru(Booru(1L, "yande.re", "https://yande.re"))
                boorus.addAll(app.boorusManager.loadBoorus())
            }
            uiThread {
                if (boorus.isNotEmpty()) {
                    Log.i(TAG, boorus.size.toString())
                    boorus.forEach {
                        val icon = builder.buildRound(it.name[0].toString(), getCustomizedColor())
                        val profileDrawerItem: ProfileDrawerItem = ProfileDrawerItem()
                                .withName(it.name)
                                .withEmail(it.url)
                                .withIcon(icon)
                        profileDrawerItem.withIdentifier(it.id)
                        header.addProfile(profileDrawerItem, it.id.toInt())
                    }
                    header.addProfile(profileSettingDrawerItem, boorus.size)
                } else {
                    header.addProfiles(profileSettingDrawerItem)
                }

                header.setActiveProfile(app.settings.activeProfile)

            }
        }
    }

    private fun getCustomizedColor(): Int {
        val customizedColors = resources.getIntArray(R.array.customizedColors)
        return customizedColors[Random().nextInt(customizedColors.size)]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moebooru)

        val metric: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metric)
        widthScreen = metric.widthPixels
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            toolbarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }

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

        loadAsync()

        drawerLayout = DrawerLayout(this)
        drawerLayout.setScrimColor(Color.TRANSPARENT)

        val aboutItem =  PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_ABOUT)
                .withName(R.string.about)
                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_drawer_copyright_24dp))
                .withIconTintingEnabled(true)

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

        previousSelectedDrawer = drawer.currentSelection

        if (savedInstanceState == null) {
            displayFragment(PostsFragment())
        } else {
            if (app.settings.isChangedNightMode) {
                drawer.setSelection(DRAWER_ITEM_SETTINGS)
                app.settings.isChangedNightMode = false
            }
        }

        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sp.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Key.NIGHT_MODE -> {
                AppCompatDelegate.setDefaultNightMode(app.settings.nightMode)
                app.settings.isChangedNightMode = true
                recreate()
            }
        }
    }

    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>?): Boolean {
        val id = drawerItem!!.identifier
        if (id == previousSelectedDrawer) {
            drawer.closeDrawer()
        } else {
            previousSelectedDrawer = id
            when (id) {
                DRAWER_ITEM_POSTS -> displayFragment(PostsFragment())
                DRAWER_ITEM_ACCOUNT -> displayFragment(AccountFragment())
                DRAWER_ITEM_DOWNLOADS -> displayFragment(DownloadsFragment())
                DRAWER_ITEM_LOCAL_GALLERY -> displayFragment(LocalGalleryFragment())
                DRAWER_ITEM_SETTINGS -> displayFragment(SettingsFragment())
                DRAWER_ITEM_ABOUT -> displayFragment(AboutFragment())
            }
        }
        return true
    }

    private fun displayFragment(fragment: ToolbarFragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_moebooru, fragment)
                .commitAllowingStateLoss()
        drawer.closeDrawer()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen) {
            drawer.closeDrawer()
        } else {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_moebooru) as ToolbarFragment
            if (!currentFragment.onBackPressed()) {
                if (currentFragment is PostsFragment) {
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
