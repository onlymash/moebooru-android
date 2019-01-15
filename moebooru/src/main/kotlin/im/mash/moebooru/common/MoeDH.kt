/*
 * Copyright (C) 2019 by onlymash <im@fiepi.me>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package im.mash.moebooru.common

import im.mash.moebooru.App
import im.mash.moebooru.detail.di.DaggerDetailComponent
import im.mash.moebooru.detail.di.DetailComponent
import im.mash.moebooru.download.di.DaggerDownloadComponent
import im.mash.moebooru.download.di.DownloadComponent
import im.mash.moebooru.main.di.DaggerMainComponent
import im.mash.moebooru.main.di.MainComponent
import im.mash.moebooru.search.di.DaggerSearchComponent
import im.mash.moebooru.search.di.SearchComponent
import javax.inject.Singleton

@Singleton
object MoeDH {

    private var mainComponent: MainComponent? = null

    fun mainComponent(): MainComponent {
        if (mainComponent == null) {
            mainComponent = DaggerMainComponent.builder().coreComponent(App.coreComponent).build()
        }
        return mainComponent as MainComponent
    }

    fun destroyMainComponent() {
        mainComponent = null
    }

    private var detailComponent: DetailComponent? = null

    fun detailComponent(): DetailComponent {
        if (detailComponent == null) {
            detailComponent = DaggerDetailComponent.builder().coreComponent(App.coreComponent).build()
        }
        return detailComponent as DetailComponent
    }

    fun destroyDetailComponent() {
        detailComponent = null
    }

    private var downloadComponent: DownloadComponent? = null

    fun downloadComponent(): DownloadComponent {
        if (downloadComponent == null) {
            downloadComponent = DaggerDownloadComponent.builder().coreComponent(App.coreComponent).build()
        }
        return downloadComponent as DownloadComponent
    }

    fun destroyDownloadComponent() {
        downloadComponent = null
    }

    private var searchComponent: SearchComponent? = null

    fun searchComponent(): SearchComponent {
        if (searchComponent == null) {
            searchComponent = DaggerSearchComponent.builder().coreComponent(App.coreComponent).build()
        }
        return searchComponent as SearchComponent
    }

    fun destroySearchComponent() {
        searchComponent = null
    }
}