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

package im.mash.moebooru

import android.content.ActivityNotFoundException
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.content.res.AppCompatResources
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem

class MainActivity : AppCompatActivity(), Drawer.OnDrawerItemClickListener {

    companion object {
        private const val DRAWER_POSTS = 0L
        private const val DRAWER_SETTINGS = 1L
        private const val DRAWER_ABOUT = 2L
    }

    internal lateinit var drawer: Drawer
    internal lateinit var header: AccountHeader
    private var previousSelectedDrawer: Long = 0    // it's actually lateinit

    private var metric: DisplayMetrics = DisplayMetrics()
    private var width: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        windowManager.defaultDisplay.getMetrics(metric)
        width = metric.widthPixels

        val profileDrawerItem = ProfileDrawerItem()
                .withName("onlymash")
                .withEmail("im@mash.im")
                .withIcon(R.mipmap.ic_launcher_round)
                .withOnDrawerItemClickListener{ view, position, drawerItem ->
                    false
                }

        val profileSettingDrawerItem = ProfileSettingDrawerItem()
                .withName(R.string.edit)
                .withIcon(R.drawable.ic_action_settings_24dp)

        header = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.background_header)
                .addProfiles(
                        profileDrawerItem,
                        profileSettingDrawerItem
                )
                .withOnAccountHeaderListener { view, profile, current ->
                    when (profile) {
                        profileDrawerItem -> Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show()
                        profileSettingDrawerItem -> Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show()
                    }
                    false
                }
                .build()

        drawer = DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withAccountHeader(header)
                .addDrawerItems(
                        PrimaryDrawerItem()
                                .withIdentifier(DRAWER_POSTS)
                                .withName(R.string.posts)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_action_posts_24dp))
                                .withIconTintingEnabled(true),
                        PrimaryDrawerItem()
                                .withIdentifier(DRAWER_SETTINGS)
                                .withName(R.string.settings)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_action_settings_24dp))
                                .withIconTintingEnabled(true)

                )
                .addStickyDrawerItems(
                        PrimaryDrawerItem()
                                .withIdentifier(DRAWER_ABOUT)
                                .withName(R.string.about)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_action_copyright_24dp))
                                .withIconTintingEnabled(true)
                )
                .withOnDrawerItemClickListener(this)
                .withActionBarDrawerToggle(true)
                .withSavedInstance(savedInstanceState)
                .withDrawerWidthPx((width*0.7).toInt())
                .build()

        if (savedInstanceState == null) {
            displayFragment(PostsFragment())
        }

        previousSelectedDrawer = drawer.currentSelection

    }

    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>?): Boolean {
        val id = drawerItem!!.identifier
        if (id == previousSelectedDrawer) {
            drawer.closeDrawer()
        } else {
            previousSelectedDrawer = id
            when (id) {
                DRAWER_POSTS -> displayFragment(PostsFragment())
                DRAWER_SETTINGS -> displayFragment(SettingsFragment())
                DRAWER_ABOUT -> displayFragment(AboutFragment())
            }
        }
        return true
    }

    private fun displayFragment(fragment: ToolbarFragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .commitAllowingStateLoss()
        drawer.closeDrawer()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen) {
            drawer.closeDrawer()
        } else {
            val currentFragment = fragmentManager.findFragmentById(R.id.fragment_main) as ToolbarFragment
            if (!currentFragment.onBackPressed()) {
                if (currentFragment is PostsFragment) {
                    super.onBackPressed()
                } else {
                    drawer.setSelection(DRAWER_POSTS)
                }
            }
        }
    }

    private val customTabsIntent by lazy {
        CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .build()
    }

    fun launchUrl(uri: Uri) = try {
        customTabsIntent.launchUrl(this, uri)
    } catch (_: ActivityNotFoundException) { }  // ignore
    fun launchUrl(uri: String) = launchUrl(Uri.parse(uri))
}
