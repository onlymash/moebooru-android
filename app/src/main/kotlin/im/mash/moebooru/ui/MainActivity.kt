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
import android.graphics.Typeface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.content.res.AppCompatResources
import android.util.DisplayMetrics
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
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.database.DatabaseBoorusManager
import im.mash.moebooru.database.database
import im.mash.moebooru.models.Boorus
import im.mash.moebooru.models.TextDrawable
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

@SuppressLint("RtlHardcoded")
class MainActivity : AppCompatActivity(), Drawer.OnDrawerItemClickListener {

    companion object {
        private const val DRAWER_POSTS = 0L
        private const val DRAWER_SETTINGS = 1L
        private const val DRAWER_ABOUT = 2L
        private val builder = getTextDrawableBuilder()
        private fun getTextDrawableBuilder(): TextDrawable.Builder {
            val builder = TextDrawable.builder()
            builder.beginConfig().width(50)
            builder.beginConfig().height(50)
            builder.beginConfig().fontSize(30)
            builder.beginConfig().useFont(Typeface.create("sans", Typeface.NORMAL))
            builder.beginConfig().withBorder(2)
            builder.beginConfig().endConfig()
            return builder as TextDrawable.Builder;
        }
    }

    private val TAG = this.javaClass.simpleName

    internal lateinit var drawer: Drawer
    private lateinit var header: AccountHeader
    private lateinit var profileSettingDrawerItem: ProfileSettingDrawerItem
    private var previousSelectedDrawer: Long = 0    // it's actually lateinit

    private val boorus: MutableList<Boorus.Booru> = mutableListOf()
    private fun loadAsync() {
        doAsync {
            boorus.addAll(DatabaseBoorusManager(database).loadBoorus())
            if (boorus.isEmpty()) {
                Log.i(TAG, "boorus.isEmpty()")
                DatabaseBoorusManager(database).saveBooru(Boorus.Booru(0L, "Konachan", "https://konachan.com"))
                DatabaseBoorusManager(database).saveBooru(Boorus.Booru(1L, "yande.re", "https://yande.re"))
                boorus.addAll(DatabaseBoorusManager(database).loadBoorus())
            }
            uiThread {
                if (boorus.isNotEmpty()) {
                    Log.i(TAG, boorus.size.toString())
                    var i = 0
                    boorus.forEach {
                        val icon = builder.buildRound(it.name!![0].toString(), getCustomizedColor())
                        val profileDrawerItem: ProfileDrawerItem = ProfileDrawerItem()
                                .withName(it.name)
                                .withEmail(it.url)
                                .withIcon(icon)
                        profileDrawerItem.withIdentifier(i.toLong())
                        header.addProfile(profileDrawerItem, i)
                        i += 1
                    }
                    header.addProfile(profileSettingDrawerItem, boorus.size)
                } else {
                    header.addProfiles(profileSettingDrawerItem)
                }

                header.setActiveProfile(Settings.activeProfile)

            }
        }
    }

    private fun getCustomizedColor(): Int {
        val customizedColors = resources.getIntArray(R.array.customizedColors)
        return customizedColors[Random().nextInt(customizedColors.size)]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profileSettingDrawerItem = ProfileSettingDrawerItem()
                .withName(R.string.edit)
                .withIcon(R.drawable.ic_action_settings_24dp)

        header = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.background_header)
                .withOnAccountHeaderListener { _, profile, _ ->
                    Settings.activeProfile = profile.identifier
                    Log.i(TAG, profile.identifier.toString())
                    false
                }
                .build()

        loadAsync()

        drawer = DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withDrawerGravity(Gravity.LEFT)
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
                .withActionBarDrawerToggleAnimated(true)
                .withSavedInstance(savedInstanceState)
                .build()

        previousSelectedDrawer = drawer.currentSelection

        if (savedInstanceState == null) {
            displayFragment(PostsFragment())
        }
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
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .commitAllowingStateLoss()
        drawer.closeDrawer()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen) {
            drawer.closeDrawer()
        } else {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_main) as ToolbarFragment
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
                .setToolbarColor(ContextCompat.getColor(this, R.color.primary))
                .build()
    }

    fun launchUrl(uri: Uri) = try {
        customTabsIntent.launchUrl(this, uri)
    } catch (_: ActivityNotFoundException) { }  // ignore
    fun launchUrl(uri: String) = launchUrl(Uri.parse(uri))
}
