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

package im.mash.moebooru.common.di


import android.content.Context
import android.content.SharedPreferences
import dagger.Component
import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.module.DatabaseModule
import im.mash.moebooru.core.module.AppModule
import im.mash.moebooru.core.module.NetworkModule
import im.mash.moebooru.core.module.StorageModule
import im.mash.moebooru.core.scheduler.Scheduler
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, StorageModule::class, DatabaseModule::class])
interface CoreComponent {

    fun context(): Context

    fun retrofit(): Retrofit

    fun httpClient(): OkHttpClient

    fun sharedPreferences(): SharedPreferences

    fun scheduler(): Scheduler

    fun database(): MoeDatabase
}