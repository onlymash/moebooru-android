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

package im.mash.moebooru.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.gson.Gson
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.model.ParamGet
import im.mash.moebooru.model.RawPost
import im.mash.moebooru.network.MoeHttpClient
import im.mash.moebooru.network.MoeResponse
import im.mash.moebooru.utils.fromJson
import im.mash.moebooru.utils.okHttpHeader

class PostsViewModel : ViewModel() {

    private val TAG = "PostsViewModel"

    private var allPosts: MutableLiveData<MutableList<RawPost>>? = null
    private var searchPosts: MutableLiveData<MutableList<RawPost>>? = null

    fun getPosts(tags: String?): MutableLiveData<MutableList<RawPost>> {
        return if (tags != null) {
            if (searchPosts == null) {
                searchPosts = MutableLiveData()
                //load from database
                loadSearchPosts(tags)
            }
            searchPosts!!
        } else {
            if (allPosts == null) {
                allPosts = MutableLiveData()
                //load from database
                loadAllPosts()
            }
            allPosts!!
        }
    }

    fun cleanPosts() {
        allPosts?.postValue(null)
        searchPosts?.postValue(null)
    }

    fun loadMorePosts(tags: String?) {
        var page = 1
        val limit = app.settings.postLimitInt
        if (allPosts != null && allPosts!!.value != null && allPosts!!.value!!.size > 0) {
            page = allPosts!!.value!!.size/limit +1
        }
        val response: MoeResponse? = getResponseData(tags, page.toLong(), limit)
        var result: MutableList<RawPost>? = null
        if (response != null) {
            try {
                result = Gson().fromJson(response.getResponseAsString().toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (result != null && result.size > 0) {
            addPostsToDatabase(result, tags)
            when (tags) {
                null -> {
                    val data: MutableList<RawPost> = allPosts!!.value!!
                    result.forEach {
                        data.add(it)
                    }
                    allPosts!!.postValue(data)
                }
                else -> {
                    val data: MutableList<RawPost> = searchPosts!!.value!!
                    result.forEach {
                        data.add(it)
                    }
                    searchPosts!!.postValue(data)
                }
            }
            Log.i(TAG, "Get new data!!")
        } else {
            app.settings.isNotMoreData = true
        }
    }

    fun refreshPosts(tags: String?) {
        val limit = app.settings.postLimitInt
        val response: MoeResponse? = getResponseData(tags, 1L, limit)
        var result: MutableList<RawPost>? = null
        if (response != null) {
            try {
                result = Gson().fromJson(response.getResponseAsString().toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (result != null && result.size > 0) {
            savePostsToDatabase(result, tags)
            when (tags) {
                null -> {
                    allPosts!!.postValue(result)
                }
                else -> {
                    searchPosts!!.postValue(result)
                }
            }
        }
    }

    private fun getResponseData(tags: String?, page: Long, limit: Int): MoeResponse? {
        var response: MoeResponse? = null
        var siteUrl: String? = null
        siteUrl = try {
            app.boorusManager.getBooru(app.settings.activeProfile).url
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        if (siteUrl != null) {
            val url = ParamGet(siteUrl, page.toString(), limit.toString(), null,
                    tags, null, null, null).makeGetUrl()
            response = MoeHttpClient.instance.get(url, null, okHttpHeader)
        }
        return response
    }

    private fun savePostsToDatabase(data: MutableList<RawPost>, tags: String?) {
        val site = app.settings.activeProfile
        if (tags != null) {
            app.searchManager.deletePosts(site, tags)
            app.searchManager.savePosts(data, site, tags)
        } else {
            app.postsManager.deletePosts(site)
            app.postsManager.savePosts(data, site)
        }
    }

    private fun addPostsToDatabase(data: MutableList<RawPost>, tags: String?) {
        val site = app.settings.activeProfile
        when (tags) {
            null -> app.postsManager.savePosts(data, site)
            else -> app.searchManager.savePosts(data, site, tags)
        }
    }

    private fun loadAllPosts() {
        var data: MutableList<RawPost>? =null
        try {
            data = app.postsManager.loadPosts(app.settings.activeProfile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (data != null) {
            Log.i(TAG, "loadAllPosts(), data size: ${data.size}")
            allPosts!!.postValue(data)
        }
    }

    private fun loadSearchPosts(tags: String) {
        var data: MutableList<RawPost>? =null
        try {
            data = app.searchManager.loadPosts(app.settings.activeProfile, tags)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (data != null) {
            searchPosts!!.postValue(data)
        }
    }
}
