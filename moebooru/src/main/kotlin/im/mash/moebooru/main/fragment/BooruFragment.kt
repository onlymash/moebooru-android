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

package im.mash.moebooru.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import androidx.core.view.ViewCompat
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.Spinner
import im.mash.moebooru.R
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.main.adapter.BooruAdapter
import im.mash.moebooru.util.logi
import im.mash.moebooru.util.takeSnackbarShort

class BooruFragment : ToolbarFragment(), MainActivity.BooruChangeListener {

    companion object {
        private const val TAG = "BooruFragment"
    }

    private lateinit var booruAdapter: BooruAdapter
    private lateinit var booruView: RecyclerView

    private lateinit var addBooru: FloatingActionButton

    private var boorus: MutableList<Booru> = mutableListOf()

    private var paddingButton = 0

    private val mainActivity: MainActivity by lazy { activity as MainActivity }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_booru, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            boorus.clear()
        }
        boorus.addAll(mainActivity.getBoorus())
        initView(view)
        mainActivity.setBooruChangeListener(this)
    }

    @SuppressLint("InflateParams")
    private fun initView(view: View) {
        toolbar.setTitle(R.string.boorus)
        addBooru = view.findViewById(R.id.add_booru)
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            paddingButton = insets.systemWindowInsetBottom
            val lp = view.layoutParams as FrameLayout.LayoutParams
            lp.setMargins(0, 0, 0, paddingButton)
            view.layoutParams = lp
            insets
        }
        booruView = view.findViewById(R.id.booru_list)
        booruView.layoutManager = LinearLayoutManager(this.requireContext(), RecyclerView.VERTICAL, false)
        booruView.addItemDecoration(DividerItemDecoration(this.requireContext(), DividerItemDecoration.VERTICAL))
        booruAdapter = BooruAdapter(this.requireContext(), boorus)
        booruView.adapter = booruAdapter
        booruAdapter.setBooruChangeListener(object : BooruAdapter.BooruChangeListener {
            override fun onBooruEdit(booru: Booru) {
                var schema = booru.scheme
                val v = layoutInflater.inflate(R.layout.layout_booru_add, null)
                val schemaSpinner: Spinner = v.findViewById(R.id.schema)
                val inputName: TextInputEditText = v.findViewById(R.id.booru_name)
                val inputDomain: TextInputEditText = v.findViewById(R.id.domain)
                val inputHashSalt: TextInputEditText = v.findViewById(R.id.hash_salt)
                inputName.setText(booru.name)
                inputDomain.setText(booru.host)
                inputHashSalt.setText(booru.hash_salt)
                when (schema) {
                    "http" -> schemaSpinner.setSelection(0)
                    else -> schemaSpinner.setSelection(1)
                }
                schemaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        when (position) {
                            0 -> schema = "http"
                            1 -> schema = "https"
                        }
                    }
                }
                val dialog = AlertDialog.Builder(this@BooruFragment.requireContext())
                        .setTitle(R.string.edit_booru)
                        .setPositiveButton(R.string.save) { _, _ ->
                            val name = inputName.text.toString()
                            val domain = inputDomain.text.toString()
                            var hashSalt = inputHashSalt.text.toString()
                            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(domain)) {
                                takeSnackbarShort(this@BooruFragment.view!!, "Booru name and domain can not be null", paddingButton)
                                return@setPositiveButton
                            }
                            if (!TextUtils.isEmpty(hashSalt) && !hashSalt.contains("your-password")) {
                                takeSnackbarShort(this@BooruFragment.view!!, "Hash salt must contain 'your-password'", paddingButton)
                                return@setPositiveButton
                            }
                            if (TextUtils.isEmpty(hashSalt)) hashSalt = ""
                            val b = Booru(null, name, schema, domain, "$schema://$domain", hashSalt)
                            mainActivity.addBooru(b)
                        }
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                dialog.apply {
                    setView(v)
                    setCanceledOnTouchOutside(true)
                    show()
                }
            }
            override fun onBooruDelete(booru: Booru) {
                AlertDialog.Builder(mainActivity)
                        .setTitle(R.string.delete_booru)
                        .setMessage("Do you confirm delete the booru?")
                        .setPositiveButton(getString(R.string.ok)) { _, _ ->
                            mainActivity.deleteBooru(booru)
                        }
                        .setNegativeButton(R.string.cancel, null)
                        .show()
            }
        })
        addBooru.setOnClickListener {
            var schema = "http"
            val v = layoutInflater.inflate(R.layout.layout_booru_add, null)
            val schemaSpinner: Spinner = v.findViewById(R.id.schema)
            val inputName: TextInputEditText = v.findViewById(R.id.booru_name)
            val inputDomain: TextInputEditText = v.findViewById(R.id.domain)
            val inputHashSalt: TextInputEditText = v.findViewById(R.id.hash_salt)
            schemaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when (position) {
                        0 -> schema = "http"
                        1 -> schema = "https"
                    }
                }
            }
            val dialog = AlertDialog.Builder(this.requireContext())
                    .setTitle(R.string.add_booru)
                    .setPositiveButton(R.string.add) { _, _ ->
                        val name = inputName.text.toString()
                        val domain = inputDomain.text.toString()
                        var hashSalt = inputHashSalt.text.toString()
                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(domain)) {
                            takeSnackbarShort(this.view!!, "Booru name and domain can not be null", paddingButton)
                            return@setPositiveButton
                        }
                        if (!TextUtils.isEmpty(hashSalt) && !hashSalt.contains("your-password")) {
                            takeSnackbarShort(this.view!!, "Hash salt must contain 'your-password'", paddingButton)
                            return@setPositiveButton
                        }
                        if (TextUtils.isEmpty(hashSalt)) hashSalt = ""
                        val booru = Booru(null, name, schema, domain, "$schema://$domain", hashSalt)
                        mainActivity.addBooru(booru)
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .create()
            dialog.apply {
                setView(v)
                setCanceledOnTouchOutside(true)
                show()
            }
        }
    }

    override fun onBooruChanged(boorus: MutableList<Booru>) {
        this.boorus = boorus
        booruAdapter.updateData(this.boorus)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.setBooruChangeListener(null)
    }
}