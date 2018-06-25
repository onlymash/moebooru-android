package im.mash.moebooru.main.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputEditText
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import im.mash.moebooru.R
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.helper.getViewModel
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.main.adapter.BooruAdapter
import im.mash.moebooru.main.viewmodel.BooruViewModel
import im.mash.moebooru.util.logi
import im.mash.moebooru.util.takeSnackbarShort

class BooruFragment : ToolbarFragment() {

    companion object {
        private const val TAG = "BooruFragment"
    }

    private lateinit var booruAdapter: BooruAdapter
    private lateinit var booruView: RecyclerView

    private lateinit var addBooru: FloatingActionButton

    private var boorus: MutableList<Booru> = mutableListOf()

    private var paddingButton = 0

    private val mainActivity: MainActivity by lazy { activity as MainActivity }
    private val booruViewModel: BooruViewModel by lazy { this.getViewModel<BooruViewModel>(mainActivity.booruViewModelFactory) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_booru, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initViewModel()
    }

    @SuppressLint("InflateParams")
    private fun initView(view: View) {
        toolbar.setTitle(R.string.boorus)
        addBooru = view.findViewById(R.id.add_booru)
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            paddingButton = insets.systemWindowInsetBottom
            view.setPadding(0, 0, 0, paddingButton)
            insets
        }
        booruView = view.findViewById(R.id.booru_list)
        booruView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayout.VERTICAL, false)
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
                val dialog = android.app.AlertDialog.Builder(context)
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
                            booruViewModel.addBooru(b)
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
                            booruViewModel.deleteBooru(booru)
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
            val dialog = android.app.AlertDialog.Builder(context)
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
                        booruViewModel.addBooru(booru)
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

    private fun initViewModel() {
        booruViewModel.booruOutcome.observe(this, Observer<Outcome<MutableList<Booru>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {

                }
                is Outcome.Success -> {
                    boorus = outcome.data
                    booruAdapter.updateData(boorus)
                    mainActivity.initHeaderItem(boorus)
                    logi(TAG, "Boorus size: ${boorus.size}")
                }
                is Outcome.Failure -> {

                }
            }
        })
        booruViewModel.loadBoorus()
    }
}